package br.com.lasanhaspec.market_service.service;

import br.com.lasanhaspec.market_service.client.FipeClient;
import br.com.lasanhaspec.market_service.domain.FipePriceSnapshot;
import br.com.lasanhaspec.market_service.dto.ExternalFipeResponseDTO;
import br.com.lasanhaspec.market_service.dto.FipePriceHistoryPointDTO;
import br.com.lasanhaspec.market_service.dto.FipePriceResponseDTO;
import br.com.lasanhaspec.market_service.repository.FipePriceSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FipeServiceTest {

    @Mock
    private FipeClient fipeClient;

    @Mock
    private FipePriceSnapshotRepository snapshotRepository;

    @InjectMocks
    private FipeService fipeService;

    private static final String BRAND = "7";
    private static final String MODEL = "179";
    private static final String YEAR = "1995-1";

    // ---------- cache fresco: nao bate na API externa ----------

    @Test
    void shouldReturnCachedPriceWithoutCallingExternalApiWhenCacheIsFresh() {
        FipePriceSnapshot freshSnapshot = new FipePriceSnapshot(
                BRAND, MODEL, YEAR, new BigDecimal("47195.00"), "julho de 2026",
                "BMW", "325i", "1995", "Gasolina", "007179-4",
                LocalDateTime.now().minusDays(5) // dentro do TTL de 20 dias
        );

        when(snapshotRepository.findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                BRAND, MODEL, YEAR)).thenReturn(Optional.of(freshSnapshot));

        FipePriceResponseDTO result = fipeService.getPrice(BRAND, MODEL, YEAR);

        assertEquals("BMW", result.getMarca());
        verifyNoInteractions(fipeClient); // nao deve ter chamado a API externa
    }

    // ---------- cache velho: busca na API externa ----------

    @Test
    void shouldCallExternalApiWhenCacheIsStale() {
        FipePriceSnapshot staleSnapshot = new FipePriceSnapshot(
                BRAND, MODEL, YEAR, new BigDecimal("40000.00"), "junho de 2026",
                "BMW", "325i", "1995", "Gasolina", "007179-4",
                LocalDateTime.now().minusDays(25) // fora do TTL de 20 dias
        );

        ExternalFipeResponseDTO external = new ExternalFipeResponseDTO();
        external.setValor("R$ 47.195,00");
        external.setMarca("BMW");
        external.setModelo("325i");
        external.setAnoModelo("1995");
        external.setCombustivel("Gasolina");
        external.setCodigoFipe("007179-4");
        external.setMesReferencia("julho de 2026");

        when(snapshotRepository.findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                BRAND, MODEL, YEAR)).thenReturn(Optional.of(staleSnapshot));
        when(fipeClient.getPrice(BRAND, MODEL, YEAR)).thenReturn(external);
        when(snapshotRepository.save(any(FipePriceSnapshot.class))).thenAnswer(inv -> inv.getArgument(0));

        FipePriceResponseDTO result = fipeService.getPrice(BRAND, MODEL, YEAR);

        verify(fipeClient).getPrice(BRAND, MODEL, YEAR);
        assertEquals("julho de 2026", result.getMesReferencia());
    }

    // ---------- sem cache nenhum: busca na API externa ----------

    @Test
    void shouldCallExternalApiWhenNoCacheExists() {
        ExternalFipeResponseDTO external = new ExternalFipeResponseDTO();
        external.setValor("R$ 47.195,00");
        external.setMarca("BMW");
        external.setModelo("325i");
        external.setAnoModelo("1995");
        external.setCombustivel("Gasolina");
        external.setCodigoFipe("007179-4");
        external.setMesReferencia("julho de 2026");

        when(snapshotRepository.findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                BRAND, MODEL, YEAR)).thenReturn(Optional.empty());
        when(fipeClient.getPrice(BRAND, MODEL, YEAR)).thenReturn(external);
        when(snapshotRepository.save(any(FipePriceSnapshot.class))).thenAnswer(inv -> inv.getArgument(0));

        fipeService.getPrice(BRAND, MODEL, YEAR);

        verify(fipeClient).getPrice(BRAND, MODEL, YEAR);
        verify(snapshotRepository).save(any(FipePriceSnapshot.class));
    }

    // ---------- mesmo mes de referencia: nao duplica linha, so atualiza ----------

    @Test
    void shouldTouchExistingSnapshotInsteadOfDuplicatingWhenSameReferenceMonth() {
        FipePriceSnapshot staleButSameMonth = new FipePriceSnapshot(
                BRAND, MODEL, YEAR, new BigDecimal("47195.00"), "julho de 2026",
                "BMW", "325i", "1995", "Gasolina", "007179-4",
                LocalDateTime.now().minusDays(25)
        );

        ExternalFipeResponseDTO external = new ExternalFipeResponseDTO();
        external.setValor("R$ 48.000,00");
        external.setMarca("BMW");
        external.setModelo("325i");
        external.setAnoModelo("1995");
        external.setCombustivel("Gasolina");
        external.setCodigoFipe("007179-4");
        external.setMesReferencia("julho de 2026"); // MESMO mes do snapshot antigo

        when(snapshotRepository.findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                BRAND, MODEL, YEAR)).thenReturn(Optional.of(staleButSameMonth));
        when(fipeClient.getPrice(BRAND, MODEL, YEAR)).thenReturn(external);
        when(snapshotRepository.save(any(FipePriceSnapshot.class))).thenAnswer(inv -> inv.getArgument(0));

        fipeService.getPrice(BRAND, MODEL, YEAR);

        ArgumentCaptor<FipePriceSnapshot> captor = ArgumentCaptor.forClass(FipePriceSnapshot.class);
        verify(snapshotRepository).save(captor.capture());

        // deve ter atualizado o MESMO objeto (mesmo preco novo), nao criado um novo
        assertEquals(new BigDecimal("48000.00"), captor.getValue().getPrice());
        assertSame(staleButSameMonth, captor.getValue());
    }

    // ---------- mes de referencia diferente: cria snapshot novo ----------

    @Test
    void shouldCreateNewSnapshotWhenReferenceMonthChanges() {
        FipePriceSnapshot oldMonth = new FipePriceSnapshot(
                BRAND, MODEL, YEAR, new BigDecimal("47195.00"), "junho de 2026",
                "BMW", "325i", "1995", "Gasolina", "007179-4",
                LocalDateTime.now().minusDays(25)
        );

        ExternalFipeResponseDTO external = new ExternalFipeResponseDTO();
        external.setValor("R$ 48.000,00");
        external.setMarca("BMW");
        external.setModelo("325i");
        external.setAnoModelo("1995");
        external.setCombustivel("Gasolina");
        external.setCodigoFipe("007179-4");
        external.setMesReferencia("julho de 2026"); // mes DIFERENTE do snapshot antigo

        when(snapshotRepository.findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                BRAND, MODEL, YEAR)).thenReturn(Optional.of(oldMonth));
        when(fipeClient.getPrice(BRAND, MODEL, YEAR)).thenReturn(external);
        when(snapshotRepository.save(any(FipePriceSnapshot.class))).thenAnswer(inv -> inv.getArgument(0));

        fipeService.getPrice(BRAND, MODEL, YEAR);

        ArgumentCaptor<FipePriceSnapshot> captor = ArgumentCaptor.forClass(FipePriceSnapshot.class);
        verify(snapshotRepository).save(captor.capture());

        // deve ser um objeto NOVO, diferente do snapshot antigo
        assertNotSame(oldMonth, captor.getValue());
        assertEquals("julho de 2026", captor.getValue().getReferenceMonth());
    }

    // ---------- API externa falha, mas tem cache: cai pro cache ----------

    @Test
    void shouldFallBackToStaleCacheWhenExternalApiFails() {
        FipePriceSnapshot staleSnapshot = new FipePriceSnapshot(
                BRAND, MODEL, YEAR, new BigDecimal("47195.00"), "junho de 2026",
                "BMW", "325i", "1995", "Gasolina", "007179-4",
                LocalDateTime.now().minusDays(30)
        );

        when(snapshotRepository.findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                BRAND, MODEL, YEAR)).thenReturn(Optional.of(staleSnapshot));
        when(fipeClient.getPrice(BRAND, MODEL, YEAR)).thenThrow(new RuntimeException("API fora do ar"));

        FipePriceResponseDTO result = fipeService.getPrice(BRAND, MODEL, YEAR);

        assertEquals("junho de 2026", result.getMesReferencia());
        verify(snapshotRepository, never()).save(any());
    }

    // ---------- API externa falha e nao tem cache nenhum: propaga erro ----------

    @Test
    void shouldThrowWhenExternalApiFailsAndNoCacheExists() {
        when(snapshotRepository.findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                BRAND, MODEL, YEAR)).thenReturn(Optional.empty());
        when(fipeClient.getPrice(BRAND, MODEL, YEAR)).thenThrow(new RuntimeException("API fora do ar"));

        assertThrows(RuntimeException.class, () -> fipeService.getPrice(BRAND, MODEL, YEAR));
    }

    // ---------- getHistory ----------

    @Test
    void shouldMapSnapshotsToHistoryPointsInOrder() {
        FipePriceSnapshot month1 = new FipePriceSnapshot(
                BRAND, MODEL, YEAR, new BigDecimal("40000.00"), "maio de 2026",
                "BMW", "325i", "1995", "Gasolina", "007179-4", LocalDateTime.now().minusDays(60));
        FipePriceSnapshot month2 = new FipePriceSnapshot(
                BRAND, MODEL, YEAR, new BigDecimal("47195.00"), "junho de 2026",
                "BMW", "325i", "1995", "Gasolina", "007179-4", LocalDateTime.now().minusDays(30));

        when(snapshotRepository.findByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtAsc(
                BRAND, MODEL, YEAR)).thenReturn(List.of(month1, month2));

        List<FipePriceHistoryPointDTO> history = fipeService.getHistory(BRAND, MODEL, YEAR);

        assertEquals(2, history.size());
        assertEquals("maio de 2026", history.get(0).getReferenceMonth());
        assertEquals("junho de 2026", history.get(1).getReferenceMonth());
    }

    @Test
    void shouldReturnEmptyListWhenNoHistoryExists() {
        when(snapshotRepository.findByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtAsc(
                BRAND, MODEL, YEAR)).thenReturn(List.of());

        List<FipePriceHistoryPointDTO> history = fipeService.getHistory(BRAND, MODEL, YEAR);

        assertTrue(history.isEmpty());
    }
}