package platform.repository;

import domain.Proposal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProposalRepository extends MongoRepository<Proposal, String>{
}
