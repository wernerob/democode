package hu.ksh.idgs.worklist.service;

import java.util.List;

import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.worklist.dto.NoteDto;
import hu.ksh.maja.core.exception.ServiceException;

public interface NoteService {

	void saveNote(NoteDto note) throws ServiceException, DaoExeption;

	List<NoteDto> getNotes(String worklistId);

}
