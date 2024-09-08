import com.mjc.school.repository.implementation.AuthorRepository;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.exceptions.ElementNotFoundException;
import com.mjc.school.service.exceptions.ValidatorException;
import com.mjc.school.service.implementation.AuthorService;
import com.mjc.school.service.mapper.AuthorMapper;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.exceptions.ErrorCodes.NOT_UNIQUE_AUTHOR_NAME;
import static com.mjc.school.service.exceptions.ErrorCodes.NO_AUTHOR_FOR_NEWS_ID;
import static com.mjc.school.service.exceptions.ErrorCodes.NO_AUTHOR_WITH_PROVIDED_ID;
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
public class AuthorServiceMockTest {
    @Mock
    private AuthorRepository authorRepository;
    private static AuthorModel authorModel;
    private AuthorMapper authorMapper= Mappers.getMapper(AuthorMapper.class);
    private CustomValidator customValidator = new CustomValidator();

    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authorModel = new AuthorModel(1L,"Morgan", LocalDateTime.now(), LocalDateTime.now().plusDays(1) );
        authorService = new AuthorService(authorRepository, authorMapper, customValidator);
    }
    @Test
    public void readAllAuthorsTest(){
        List<AuthorModel> authorModelsList = List.of(new AuthorModel(1L, "Josephina", LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        when(authorRepository.readAll(anyInt(), anyInt(),anyString())).thenReturn(authorModelsList);
        List<AuthorDtoResponse> result = authorService.readAll(0, 5,"name,dsc");

        assertEquals(authorModelsList.size(), result.size());
       assertEquals(authorModelsList.get(0).getName(), result.get(0).name());
    }
    @Test
    public void readAuthorByIdTest(){
        when(authorRepository.readById(anyLong())).thenReturn(Optional.of(authorModel));
        AuthorDtoResponse result = authorService.readById(1L);
      assertEquals(authorModel.getName(), result.name());
    }

    @Test
    public void readAuthorByIdWithElementNotFoundException(){
        when(authorRepository.readById(anyLong())).thenReturn(Optional.empty());
        ElementNotFoundException thrown = Assertions.assertThrows(ElementNotFoundException.class , () -> authorService.readById(1L));
        assertEquals(thrown.getMessage(), String.format(NO_AUTHOR_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }
    @Test
    public void createAuthorTest(){
        when(authorRepository.create(any())).thenReturn(authorModel);
        AuthorDtoResponse result = authorService.create(new AuthorDtoRequest("Morgan"));
        assertEquals(authorModel.getName(), result.name());
    }
    @Test
    public void createValidationFailedAuthorTest(){
        lenient().when(authorRepository.create(any())).thenReturn(authorModel);
        AuthorDtoRequest result = new AuthorDtoRequest("Mo");
      assertThrows(ValidationException.class , () -> authorService.create(result));
    }
    @Test
    public void createAuthorWithNonUniqueNameTest() {
        when(authorRepository.readAuthorByName(anyString())).thenReturn(Optional.of(authorModel));
      AuthorDtoRequest result = new AuthorDtoRequest("Morgan");
        Exception exception = assertThrows(ValidatorException.class , () -> authorService.create(result));
        verify(authorRepository, never()).create(any());
        assertEquals(exception.getMessage(), NOT_UNIQUE_AUTHOR_NAME.getErrorMessage());
    }

    @Test
    public void deleteAuthorTest(){
        when(authorRepository.existById(anyLong())).thenReturn(true);
        authorService.deleteById(1L);
    }

    @Test
    public void deleteNonexistentAuthor(){
        lenient().when(authorRepository.existById(anyLong())).thenReturn(false);
        ElementNotFoundException exception =  Assertions.assertThrows(ElementNotFoundException.class , () -> authorService.deleteById(1L));
        assertEquals(exception.getMessage(), String.format(NO_AUTHOR_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }
    @Test
    public void readAuthorByNewsIdTest(){
        when(authorRepository.readAuthorByNewsId(anyLong())).thenReturn(Optional.of(authorModel));
        AuthorDtoResponse result = authorService.readAuthorByNewsId(1L);
        assertEquals(authorModel.getName(), result.name());
    }
    @Test
    public void readNonexistentAuthorByNewsIdTest(){
        when(authorRepository.readAuthorByNewsId(anyLong())).thenReturn(Optional.empty());
        ElementNotFoundException exception = Assertions.assertThrows(ElementNotFoundException.class , () -> authorService.readAuthorByNewsId(1L));
        assertEquals(exception.getMessage(), String.format(NO_AUTHOR_FOR_NEWS_ID.getErrorMessage(), 1L));
    }


    }
