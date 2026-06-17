package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.enums.IssueStatus;
import br.com.lasanhaspec.carservice.domain.enums.VoteType;
import br.com.lasanhaspec.carservice.domain.models.ChronicIssue;
import br.com.lasanhaspec.carservice.domain.models.IssueVote;
import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.exception.BusinessException;
import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.repository.ChronicIssueRepository;
import br.com.lasanhaspec.carservice.repository.IssueVoteRepository;
import br.com.lasanhaspec.carservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IssueVoteService {

    private final IssueVoteRepository issueVoteRepository;
    private final ChronicIssueRepository chronicIssueRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(IssueVoteService.class);



    public IssueVoteService(IssueVoteRepository issueVoteRepository,
                            ChronicIssueRepository chronicIssueRepository,
                            UserRepository userRepository){

        this.issueVoteRepository = issueVoteRepository;
        this.chronicIssueRepository = chronicIssueRepository;
        this.userRepository = userRepository;
    }



    @Transactional
    public void vote(Long issueId, String email, VoteType voteType) {
        ChronicIssue chronicIssue = chronicIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long userId = user.getId();

        Optional<IssueVote> existingVote =
                issueVoteRepository.findByIssueIdAndUserId(issueId, userId);

        if (existingVote.isEmpty()) {

            IssueVote newIssueVote = new IssueVote();
            newIssueVote.setIssue(chronicIssue);
            newIssueVote.setUserId(userId);
            newIssueVote.setVoteType(voteType);
            issueVoteRepository.save(newIssueVote);

            if (voteType == VoteType.USEFUL) {
                chronicIssue.setUsefulVotes(chronicIssue.getUsefulVotes() + 1);
            } else {
                chronicIssue.setNotUsefulVotes(chronicIssue.getNotUsefulVotes() + 1);
            }

        } else {

            IssueVote vote = existingVote.get();

            if (vote.getVoteType() == voteType) {
                throw new BusinessException("You have already voted this way on this issue");
            }

            if (vote.getVoteType() == VoteType.USEFUL) {
                chronicIssue.setUsefulVotes(chronicIssue.getUsefulVotes() - 1);
            } else {
                chronicIssue.setNotUsefulVotes(chronicIssue.getNotUsefulVotes() - 1);
            }

            if (voteType == VoteType.USEFUL) {
                chronicIssue.setUsefulVotes(chronicIssue.getUsefulVotes() + 1);
            } else {
                chronicIssue.setNotUsefulVotes(chronicIssue.getNotUsefulVotes() + 1);
            }

            vote.setVoteType(voteType);
            issueVoteRepository.save(vote);
        }

        if (chronicIssue.getUsefulVotes() >= 10
                && chronicIssue.getStatus() == IssueStatus.PENDING) {
            chronicIssue.setStatus(IssueStatus.IN_REVIEW);

            log.info("Chronic issue {} promoted to IN_REVIEW with {} useful votes",
                    chronicIssue.getId(), chronicIssue.getUsefulVotes());
        }

        chronicIssueRepository.save(chronicIssue);
    }


}


// voto individual
// IssueVote
// -- issue
// -- userId
// -- voteType


//agregador estatistico
// ChronicIssue
//-- usefulVotes
// notUsefulVotes


//testar
// contador nao negativo
//troca de voto ajusta os dois lados
//voto dupplicado nao cria nova linha
// voto novo incrementa corretamente?
//remoção de voto decrementa


// voto novo
//troca de voto
//voto igual ao anterior
// usuario inexistente ou issue inexistente
