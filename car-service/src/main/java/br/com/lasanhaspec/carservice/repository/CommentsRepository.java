package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.models.Comments;
import br.com.lasanhaspec.carservice.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface CommentsRepository  extends JpaRepository <Comments, Long>{

    List<Comments> findByChronicIssueIdOrderByCreatedAtAsc(Long chronicIssueId);

    User save(Comments comment);

    void delete(Comments comment);
}
