package hu.ksh.idgs.worklist.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dao.NoteRepository;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dto.NoteDto;
import hu.ksh.idgs.worklist.model.NoteEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.service.NoteService;
import hu.ksh.maja.core.annotation.TransactionRequired;
import hu.ksh.maja.core.exception.ServiceException;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@TransactionRequired
	@Override
	public void saveNote(final NoteDto note) throws ServiceException, DaoExeption {

		NoteEntity noteEntity = new NoteEntity();
		final WorklistItemEntity worklistItem = getWorklistItem(note.getWorklistId());

		noteEntity.setNote(note.getNote());
		noteEntity.setSaveDate(note.getSaveDate());
		noteEntity.setWorklistItem(worklistItem);

		noteEntity = this.noteRepository.save(noteEntity);

		if (worklistItem != null) {
			this.updateWorklistItem(worklistItem, noteEntity);
		}

	}

	@Override
	public List<NoteDto> getNotes(final String worklistId) {
		return this.noteRepository.getNotes(IdgsEntityId.fromString(worklistId));
	}

	private WorklistItemEntity getWorklistItem(final IdgsEntityId worklistItemId) {
		final Optional<WorklistItemEntity> worklistItemOptional = this.worklistItemRepository.findById(worklistItemId);

		if (worklistItemOptional.isPresent()) {
			return worklistItemOptional.get();
		}

		return null;
	}

	private void updateWorklistItem(final WorklistItemEntity worklistItem, final NoteEntity latestNote)
			throws DaoExeption {

		worklistItem.setLatestNote(latestNote);

		this.worklistItemRepository.save(worklistItem);

	}

}
