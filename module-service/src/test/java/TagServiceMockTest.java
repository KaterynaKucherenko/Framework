import com.mjc.school.repository.implementation.TagRepository;
import com.mjc.school.repository.model.TagModel;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.exceptions.ElementNotFoundException;
import com.mjc.school.service.exceptions.ValidatorException;
import com.mjc.school.service.implementation.TagsService;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.validation.CustomValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.exceptions.ErrorCodes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceMockTest {
    @Mock
    private TagRepository tagRepository;
    private TagMapper tagMapper = Mappers.getMapper(TagMapper.class);

    private static TagModel tagModel;

    private CustomValidator customValidator = new CustomValidator();
    @InjectMocks
    private TagsService tagsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tagModel = new TagModel("programming");
        tagsService = new TagsService(tagRepository, tagMapper, customValidator);
    }

    @Test
    public void readAllTagsTest() {
        List<TagModel> tagModelsList = List.of(new TagModel("programming"), new TagModel("auditing"));
        when(tagRepository.readAll(anyInt(), anyInt(), anyString())).thenReturn(tagModelsList);
        List<TagDtoResponse> result = tagsService.readAll(0, 5, "name,dsc");

        assertEquals(tagModelsList.size(), result.size());
        assertEquals(tagModelsList.get(0).getName(), result.get(0).name());
        assertEquals(tagModelsList.get(1).getName(), result.get(1).name());
    }

    @Test
    public void readTagByIdTest() {
        when(tagRepository.readById(anyLong())).thenReturn(Optional.of(tagModel));
        TagDtoResponse result = tagsService.readById(1L);
        assertEquals(tagModel.getName(), result.name());
    }

    @Test
    public void readTagByIdWithElementNotFoundException() {
        when(tagRepository.readById(anyLong())).thenReturn(Optional.empty());
        ElementNotFoundException exception = Assertions.assertThrows(ElementNotFoundException.class, () -> tagsService.readById(1L));
        assertEquals(exception.getMessage(), String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }

    @Test
    public void createTagTest() {
        when(tagRepository.create(any())).thenReturn(tagModel);
        TagDtoResponse result = tagsService.create(new TagDtoRequest("programming"));
        assertEquals(tagModel.getName(), result.name());
    }

    @Test
    public void createValidationFailedTagTest() {
        lenient().when(tagRepository.create(any())).thenReturn(tagModel);
        TagDtoRequest result = new TagDtoRequest("pr");
        assertThrows(ValidationException.class, () -> tagsService.create(result));
    }

    @Test
    public void createTagWithNonUniqueNameTest() {
        when(tagRepository.readTagByName(anyString())).thenReturn(Optional.of(tagModel));
        TagDtoRequest result = new TagDtoRequest("programming");
        Exception exception = assertThrows(ValidatorException.class, () -> tagsService.create(result));
        verify(tagRepository, never()).create(any());
        assertEquals(exception.getMessage(), NOT_UNIQUE_TAGS_NAME.getErrorMessage());
    }

    @Test
    public void deleteTagTest() {
        when(tagRepository.existById(anyLong())).thenReturn(true);
        tagsService.deleteById(1L);
    }

    @Test
    public void deleteNonexistentTag() {
        lenient().when(tagRepository.existById(anyLong())).thenReturn(false);
        ElementNotFoundException exception = Assertions.assertThrows(ElementNotFoundException.class, () -> tagsService.deleteById(1L));
        assertEquals(exception.getMessage(), String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }

    @Test
    public void readListOfTagsByNewsIdTest() {
        List<TagModel> listOfTags = List.of(new TagModel("programming"), new TagModel("auditing"));
        when(tagRepository.readListOfTagsByNewsId((anyLong()))).thenReturn(listOfTags);
        List<TagDtoResponse> result = tagsService.readListOfTagsByNewsId(1L);
        assertEquals(listOfTags.get(0).getName(), result.get(0).name());
        assertEquals(listOfTags.get(1).getName(), result.get(1).name());
    }

}

