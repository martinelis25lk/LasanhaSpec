package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.domain.models.UserVehicle;
import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.domain.models.VehicleImage;
import br.com.lasanhaspec.carservice.dto.CreateUserVehicleDTO;
import br.com.lasanhaspec.carservice.dto.VehicleCardDTO;
import br.com.lasanhaspec.carservice.exception.BusinessException;
import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.infrastructure.storage.S3StorageService;
import br.com.lasanhaspec.carservice.mappers.VehicleCardMapper;
import br.com.lasanhaspec.carservice.repository.UserRepository;
import br.com.lasanhaspec.carservice.repository.UserVehicleRepository;
import br.com.lasanhaspec.carservice.repository.VehicleCatalogRepository;
import br.com.lasanhaspec.carservice.repository.VehicleImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;



import java.util.List;

@Service
public class UserVehicleService {


    private final UserVehicleRepository userVehicleRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final S3StorageService storageService;
    private final VehicleCatalogRepository vehicleCatalogRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserVehicleService.class);

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5mb

    public UserVehicleService(
            UserVehicleRepository userVehicleRepository,
            S3StorageService storageService,
            VehicleCatalogRepository vehicleCatalogRepository,
            VehicleImageRepository vehicleImageRepository,
            UserRepository userRepository
            ){
        this.userVehicleRepository = userVehicleRepository;
        this.storageService = storageService;
        this.vehicleCatalogRepository = vehicleCatalogRepository;
        this.vehicleImageRepository = vehicleImageRepository;
        this.userRepository = userRepository;
    }



    public String uploadVehicleImage(Long vehicleId, MultipartFile file, String email) {

        UserVehicle vhc = getOwnedVehicle(vehicleId, email);

        UserVehicle vehicle = userVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("vehicle not found UserVehicleService KKKKK"));

        long count = vehicleImageRepository.countByUserVehicleId(vehicleId);

        if (count >= 5) {
            throw new BusinessException("Only 5 images per vehicle");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("Arquivo muito grande");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Apenas imagens são permitidas");
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !(filename.toLowerCase().endsWith(".jpg") ||
                        filename.endsWith(".jpeg") ||
                        filename.endsWith(".png") ||
                        filename.endsWith(".webp"))) {
            throw new IllegalArgumentException("Formato de imagem não suportado");
        }

        S3StorageService.UploadResult result = storageService.uploadFile(file);

        VehicleImage image = new VehicleImage();
        image.setUserVehicle(vehicle);
        image.setImageUrl(result.getUrl());
        image.setS3Key(result.getKey());

       // boolean isFirstImage = vehicle.getImages().isEmpty();
       // image.setPrimaryImage(isFirstImage);

        boolean hasImages = vehicleImageRepository.existsByUserVehicleId(vehicleId);
        image.setPrimaryImage(!hasImages);

        vehicleImageRepository.save(image);
        System.out.println("SALVOU IMAGEM NO BANCO");

        return result.getUrl();
    }



    public VehicleCardDTO getVehicleByIdForAuthenticatedUser(Long vehicleId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserVehicle vehicle = userVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getUserId().equals(user.getId())) {
            throw new BusinessException("You do not have permission to access this vehicle");
        }

        return VehicleCardMapper.toDTO(vehicle);
    }


    public VehicleCardDTO updateVehicleFromAuthenticatedUser(
            Long vehicleId,
            CreateUserVehicleDTO dto,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserVehicle vehicle = userVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getUserId().equals(user.getId())) {
            throw new BusinessException("You do not have permission to update this vehicle");
        }

        VehicleCatalogModel model = vehicleCatalogRepository.findById(dto.getVehicleCatalogModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Catalog vehicle not found"));

        vehicle.setVehicleCatalogModel(model);
        vehicle.setNickname(dto.getNickName());
        vehicle.setCurrentHorsePower(dto.getCurrentHorsePower());
        vehicle.setCurrentTorque(dto.getCurrentTorque());
        vehicle.setCurrentWeight(dto.getCurrentWeight());

        UserVehicle saved = userVehicleRepository.save(vehicle);

        return VehicleCardMapper.toDTO(saved);
    }



    public void deleteVehicleFromAuthenticatedUser(Long vehicleId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserVehicle vehicle = userVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getUserId().equals(user.getId())) {
            throw new BusinessException("You do not have permission to delete this vehicle");
        }

        userVehicleRepository.delete(vehicle);
    }



    public Long createUserVehicle(CreateUserVehicleDTO dto, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->
                        new ResourceNotFoundException("User not found"));

        VehicleCatalogModel model = vehicleCatalogRepository
                .findById(dto.getVehicleCatalogModelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Catalog vehicle not found, create first a a vehicle in the catalog"));

        UserVehicle vehicle = new UserVehicle();
        vehicle.setUserId(user.getId());
        vehicle.setVehicleCatalogModel(model);
        vehicle.setNickname(dto.getNickName());
        vehicle.setCurrentHorsePower(dto.getCurrentHorsePower());
        vehicle.setCurrentWeight(dto.getCurrentWeight());
        vehicle.setCurrentTorque(dto.getCurrentTorque());
        vehicle.setActive(true);
        return userVehicleRepository.save(vehicle).getId();
    }





    public void deleteVehicleImage(Long vehicleId, Long imageId, String email){

        UserVehicle vehicle = getOwnedVehicle(vehicleId, email);

        VehicleImage image = vehicleImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (!image.getUserVehicle().getId().equals(vehicle.getId())) {
            throw new BusinessException("Image does not belong to this vehicle");
        }


        if (image.getUserVehicle() == null ||
                !image.getUserVehicle().getId().equals(vehicleId)) {
            throw new BusinessException("Image does not belong to vehicle");
        }

        log.info("User {} added vehicle '{}' to garage",
                email,  vehicle.getNickname());

        storageService.deleteFile(image.getS3Key());
        vehicleImageRepository.delete(image);

    }


    public List<VehicleCardDTO> getUserVehicles(){
        return userVehicleRepository.findAll()
                .stream()
                .map(VehicleCardMapper::toDTO)
                .toList();
    }


    public VehicleCardDTO getVehicleById(Long id) {
        return userVehicleRepository.findById(id)
                .map(VehicleCardMapper::toDTO)
                .orElseThrow(()-> new ResourceNotFoundException("vehicle not found"));
    }



    public VehicleCardDTO udpateVehicle(Long id, CreateUserVehicleDTO dto){

        //verifica se o veiculo existe
        UserVehicle vehicle = userVehicleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("vehicle not found"));

        vehicle.setNickname(dto.getNickName());
        vehicle.setCurrentHorsePower(dto.getCurrentHorsePower());
        vehicle.setCurrentWeight(dto.getCurrentWeight());
        vehicle.setCurrentTorque(dto.getCurrentTorque());

        userVehicleRepository.save(vehicle);

        return VehicleCardMapper.toDTO(vehicle);

    }


    public void deleteVehicle(Long id){
        UserVehicle vehicle = userVehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vehicle not found"));

        userVehicleRepository.delete(vehicle);

    }


    @Transactional
    public void setPrimaryImage(Long vehicleId, Long imageId, String email) {

        UserVehicle vehicle = getOwnedVehicle(vehicleId, email);
        VehicleImage image = vehicleImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        if (!image.getUserVehicle().getId().equals(vehicle.getId())) {
            throw new BusinessException("Image does not belong to this vehicle");
        }
        vehicle.getImages().forEach(img -> img.setPrimaryImage(false));

        image.setPrimaryImage(true);

        vehicleImageRepository.saveAll(vehicle.getImages());
        vehicleImageRepository.save(image);

    }



    public String getPrimaryImageUrl(Long vehicleId){
        VehicleImage image_url = new VehicleImage();



        return vehicleImageRepository
                .findByUserVehicleIdAndPrimaryImageTrue(vehicleId)
                .map(VehicleImage::getImageUrl)
                .orElse(null);

    }


    public List<VehicleCardDTO> getVehiclesFromAuthenticatedUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<UserVehicle> vehicles =
                userVehicleRepository.findByUserId(user.getId());

        return vehicles.stream()
                .map(VehicleCardMapper::toDTO)
                .toList();
    }


    private UserVehicle getOwnedVehicle(Long vehicleId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserVehicle vehicle = userVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getUserId().equals(user.getId())) {
            throw new BusinessException("You do not have permission to access this vehicle");
        }

        return vehicle;
    }



    public List<VehicleCardDTO> getFeedVehicles(){
        return userVehicleRepository.findFeedVehiclesWithImages()
                .stream()
                .map(VehicleCardMapper::toDTO)
                .toList();

    }




}

