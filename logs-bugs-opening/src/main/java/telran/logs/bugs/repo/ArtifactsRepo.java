package telran.logs.bugs.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Programmer;
public interface ArtifactsRepo extends JpaRepository<Artifact, String> {
	@Query(value = "Select a.programmer from Artifact a where a.artifactId = :artifactId")
	Programmer findByArtifactId(String artifactId);
}
