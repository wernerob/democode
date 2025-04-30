package hu.ksh.idgs.worklist.api.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.NoteApiModel;
import hu.ksh.idgs.worklist.dto.NoteDto;

@Mapper
public interface NoteMapper extends CommonMapper {

	@Mapping(source = "note.worklistId", target = "worklistId")
	@Mapping(source = "note.note", target = "note")
	@Mapping(source = "note.saveDate", target = "saveDate")
	NoteApiModel getNoteApiModel(NoteDto note);

	@Mapping(source = "noteApi.worklistId", target = "worklistId")
	@Mapping(source = "noteApi.note", target = "note")
	@Mapping(source = "noteApi.saveDate", target = "saveDate")
	NoteDto getNoteDto(NoteApiModel noteApi);

	List<NoteApiModel> getNoteApiModels(List<NoteDto> noteList);

	List<NoteDto> getNoteDtos(List<NoteApiModel> noteApiList);

}
