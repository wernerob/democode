package hu.ksh.idgs.worklist.dao;

import java.util.List;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.NoteDto;
import hu.ksh.idgs.worklist.model.NoteEntity;

public interface NoteRepository extends IdgsRepository<NoteEntity> {

	List<NoteDto> getNotes(IdgsEntityId worklistId);

}
