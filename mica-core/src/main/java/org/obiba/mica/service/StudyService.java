package org.obiba.mica.service;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.obiba.mica.domain.Study;
import org.obiba.mica.domain.StudyState;
import org.obiba.mica.repository.StudyStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudyService {

  private static final Logger log = LoggerFactory.getLogger(StudyService.class);

  @Inject
  private StudyStateRepository studyStateRepository;

  @Inject
  private GitService gitService;

  public void save(@NotNull Study study) {
    StudyState studyState = findStudyState(study);
    gitService.save(studyState.getId(), study);

    studyState.setName(study.getName());
    studyStateRepository.save(studyState);
  }

  @NotNull
  private StudyState findStudyState(Study study) {
    if(study.getId() == null) {
      StudyState studyState = new StudyState();
      studyStateRepository.save(studyState);
      study.setId(studyState.getId());
      return studyState;
    }
    StudyState studyState = studyStateRepository.findOne(study.getId());
    if(studyState == null) throw NoSuchStudyException.withId(study.getId());
    return studyState;
  }

  @NotNull
  public StudyState findStateByStudy(@NotNull Study study) throws NoSuchStudyException {
    return findStateById(study.getId());
  }

  @NotNull
  public StudyState findStateById(@NotNull String id) throws NoSuchStudyException {
    StudyState studyState = studyStateRepository.findOne(id);
    if(studyState == null) throw NoSuchStudyException.withId(id);
    return studyState;
  }

  @NotNull
  public Study findById(@NotNull String id) throws NoSuchStudyException {
    // ensure study exists
    findStateById(id);
    return gitService.read(id, Study.class);
  }

  public List<StudyState> findAllStates() {
    return studyStateRepository.findAll();
  }

//  public void delete(@NotNull String id) {
//    studyRepository.delete(id);
//  }

}
