package hu.ksh.idgs.worklist.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dao.NoteRepository;
import hu.ksh.idgs.worklist.dto.NoteDto;
import hu.ksh.idgs.worklist.model.NoteEntity;
import hu.ksh.idgs.worklist.model.NoteEntity_;
import hu.ksh.maja.core.model.MajaEntity_;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository("noteRepository")
public class NoteRepositoryImpl extends AbstractRepositoryImpl<NoteEntity> implements NoteRepository {

	public NoteRepositoryImpl() {
		super(NoteEntity.class);
	}

	@Override
	public List<NoteDto> getNotes(final IdgsEntityId worklistId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<NoteDto> criteriaQuery = cb.createQuery(NoteDto.class);
		final Root<NoteEntity> note = criteriaQuery.from(NoteEntity.class);

		criteriaQuery.multiselect(note.get(NoteEntity_.WORKLIST_ITEM).get(MajaEntity_.ID), note.get(NoteEntity_.NOTE),
				note.get(NoteEntity_.SAVE_DATE));

		final List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(note.get(NoteEntity_.WORKLIST_ITEM).get(MajaEntity_.ID), worklistId));

		criteriaQuery.where(predicates.stream().toArray(Predicate[]::new));
		criteriaQuery.orderBy(cb.desc(note.get(NoteEntity_.SAVE_DATE)));

		final TypedQuery<NoteDto> query = createQuery(criteriaQuery);
		query.setMaxResults(4);

		return query.getResultList();
	}

}
