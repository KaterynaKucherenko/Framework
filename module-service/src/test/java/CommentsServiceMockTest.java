import com.mjc.school.repository.implementation.CommentRepository;
import com.mjc.school.repository.implementation.NewsRepository;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.CommentModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.exceptions.ElementNotFoundException;
import com.mjc.school.service.implementation.CommentService;
import com.mjc.school.service.mapper.CommentMapper;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.exceptions.ErrorCodes.NO_COMMENT_WITH_PROVIDED_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentsServiceMockTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private NewsRepository newsRepository;

    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    private static CommentModel commentModel;
    private static NewsModel newsModel;
    private CustomValidator customValidator = new CustomValidator();
    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        newsModel = new NewsModel(3L, "The Integrity", "The Populist Wave and Its Discontents", new AuthorModel(1L, "Barbara", LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        commentModel = new CommentModel(1L, "Incredible!", newsModel, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        commentService = new CommentService(commentRepository, commentMapper, newsRepository, customValidator);

    }

    @Test
    public void readAllCommentsTest() {
        List<CommentModel> commentModelList = List.of(commentModel, new CommentModel(2L, "Unbelievable!", newsModel, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        when(commentRepository.readAll(anyInt(), anyInt(), anyString())).thenReturn(commentModelList);
        List<CommentDtoResponse> result = commentService.readAll(0, 5, "content,desc");
        assertEquals(commentModelList.size(), result.size());
        assertEquals(commentModelList.get(0).getContent(), result.get(0).content());
        assertEquals(commentModelList.get(1).getContent(), result.get(1).content());
    }

    @Test
    public void readCommentByIdTest() {
        when(commentRepository.readById(anyLong())).thenReturn(Optional.of(commentModel));
        CommentDtoResponse result = commentService.readById(1L);
        assertEquals(commentModel.getContent(), result.content());
    }

    @Test
    public void readCommentByIdWithElementNotFoundException() {
        when(commentRepository.readById(anyLong())).thenReturn(Optional.empty());
        ElementNotFoundException thrown = Assertions.assertThrows(ElementNotFoundException.class, () -> commentService.readById(1L));
        assertEquals(thrown.getMessage(), String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }


    @Test
    public void createCommentTest() {
        when(newsRepository.readById(anyLong())).thenReturn(Optional.of(newsModel));
        when(commentRepository.create(any())).thenReturn(commentModel);
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("Incredible!", 3L);
        CommentDtoResponse result = commentService.create(commentDtoRequest);
        assertEquals(commentModel.getContent(), result.content());
    }

    @Test
    public void deleteCommentTest() {
        when(commentRepository.existById(anyLong())).thenReturn(true);
        commentService.deleteById(1L);
    }

    @Test
    public void deleteNonexistentComment() {
        lenient().when(commentRepository.existById(anyLong())).thenReturn(false);
        ElementNotFoundException exception = Assertions.assertThrows(ElementNotFoundException.class, () -> commentService.deleteById(1L));
        assertEquals(exception.getMessage(), String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }

    @Test
    public void readListOfCommentsByNewsIdTest() {
        List<CommentModel> commentModelList = List.of(commentModel, new CommentModel(2L, "Unbelievable!", newsModel, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        when(commentRepository.readListOfCommentsByNewsId(anyLong())).thenReturn(commentModelList);
        List<CommentDtoResponse> result = commentService.readListOfCommentsByNewsId(1L);
        assertEquals(commentModelList.size(), result.size());
        assertEquals(commentModelList.get(0).getContent(), result.get(0).content());
        assertEquals(commentModelList.get(1).getContent(), result.get(1).content());
    }
}
