package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.constants.Status;
import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.ProductMapper;
import com.marvin.campustrade.repository.*;
import com.marvin.campustrade.service.ImageService;
import com.marvin.campustrade.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private FavouriteRepository favouriteRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private ImageService imageService;
    @Mock private ProductMapper productMapper;
    @Mock private ProductRepository productRepository;
    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private ConversationRepository conversationRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UsersBlockRepository usersBlockRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_savesDraftProductForCurrentUser() {
        // arrange
        Users user = new Users();
        user.setId(1L);

        ProductDTO.CreateRequest request = new ProductDTO.CreateRequest();

        Product product = new Product();
        Product savedProduct = new Product();

        when(userService.getCurrentUser()).thenReturn(user);
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct))
                .thenReturn(new ProductDTO.Response());

        // act
        ProductDTO.Response response = productService.createProduct(request);

        // assert
        assertNotNull(response);
        assertEquals(Status.DRAFT, product.getStatus());
        assertEquals(user, product.getUser());

        verify(productRepository).save(product);
    }

    @Test
    void saveImages_savesImageEntities() {
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.saveImages(1L, List.of("img1", "img2"));

        verify(imageRepository, times(2)).save(any(Image.class));
    }

    @Test
    void publishProduct_whenImagesExist_setsStatusAvailable() {
        Product product = new Product();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(imageRepository.countByProduct(product)).thenReturn(1L);

        productService.publishProduct(1L);

        assertEquals(Status.AVAILABLE, product.getStatus());
    }

    @Test
    void publishProduct_whenNoImages_throwsException() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(imageRepository.countByProduct(product))
                .thenReturn(0L);

        assertThrows(IllegalStateException.class,
                () -> productService.publishProduct(1L));
    }

    @Test
    void deleteProduct_whenNotOwner_throwsException() {
        Users owner = new Users();
        owner.setId(1L);

        Product product = new Product();
        product.setUser(owner);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(1L, 2L));

        verify(productRepository, never()).delete(any());
    }

    @Test
    void deleteProduct_whenOwner_deletesProductAndImages() {
        Users owner = new Users();
        owner.setId(1L);

        Product product = new Product();
        product.setUser(owner);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L, 1L);

        verify(imageService).deleteImagesByProduct(product);
        verify(productRepository).delete(product);
    }

    @Test
    void updateProduct_whenNotOwner_throwsException() {
        Users owner = new Users();
        owner.setId(1L);

        Product product = new Product();
        product.setUser(owner);

        ProductDTO.UpdateRequest request = new ProductDTO.UpdateRequest();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> productService.updateProduct(1L, request, 2L));

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_whenOwnerAndBlankFields_blanksAreIgnored() {
        Users owner = new Users();
        owner.setId(1L);

        Product product = new Product();
        product.setUser(owner);

        ProductDTO.UpdateRequest request = new ProductDTO.UpdateRequest();
        request.setTitle("   ");        // blank
        request.setDescription("");     // blank

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(new ProductDTO.Response());

        ProductDTO.Response response =
                productService.updateProduct(1L, request, 1L);

        assertNotNull(response);
        assertNull(request.getTitle());
        assertNull(request.getDescription());

        verify(productRepository).save(product);
    }

    @Test
    void markAsSold_whenNotOwner_throwsException() {
        Users owner = new Users();
        owner.setId(1L);

        Users other = new Users();
        other.setId(2L);

        Product product = new Product();
        product.setUser(owner);
        product.setStatus(Status.AVAILABLE);

        Conversation conversation = new Conversation();
        conversation.setProduct(product);
        conversation.setUser1(owner);
        conversation.setUser2(other);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conversation));
        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> productService.markAsSold(1L, 999L));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void markAsSold_whenAlreadySold_throwsException() {
        Users seller = new Users();
        seller.setId(1L);

        Users buyer = new Users();
        buyer.setId(2L);

        Product product = new Product();
        product.setUser(seller);
        product.setStatus(Status.SOLD);

        Conversation conversation = new Conversation();
        conversation.setProduct(product);
        conversation.setUser1(seller);
        conversation.setUser2(buyer);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conversation));
        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> productService.markAsSold(1L, 1L));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void markAsSold_whenValid_createsTransactionAndMarksSold() {
        Users seller = new Users();
        seller.setId(1L);

        Users buyer = new Users();
        buyer.setId(2L);

        Product product = new Product();
        product.setUser(seller);
        product.setStatus(Status.AVAILABLE);

        Conversation conversation = new Conversation();
        conversation.setProduct(product);
        conversation.setUser1(seller);
        conversation.setUser2(buyer);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conversation));
        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));
        when(userService.getCurrentUser()).thenReturn(seller);
        when(productRepository.save(product))
                .thenReturn(product);
        when(productMapper.toResponse(product))
                .thenReturn(new ProductDTO.Response());

        ProductDTO.Response response =
                productService.markAsSold(1L, 1L);

        assertEquals(Status.SOLD, product.getStatus());
        assertNotNull(response);

        verify(transactionRepository).save(any());
        verify(productRepository).save(product);
    }

    @Test
    void getSellerProducts_whenSellerNotFound_throwsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> productService.getSellerProducts(99L, 1L));
    }

    @Test
    void getSellerProducts_whenSellerExists_returnsProducts() {
        Users seller = new Users();
        seller.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setUser(seller);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findAllByUserId(1L))
                .thenReturn(List.of(product));

        when(imageRepository.findAllByProductIn(any()))
                .thenReturn(List.of());

        when(userService.getCurrentUser()).thenReturn(seller);
        when(favouriteRepository.findFavouriteProductIds(any(), any()))
                .thenReturn(Set.of());

        when(productMapper.toResponse(product))
                .thenReturn(new ProductDTO.Response());

        List<ProductDTO.Response> responses =
                productService.getSellerProducts(1L, 1L);

        assertEquals(1, responses.size());
    }

    @Test
    void getProductById_whenAccessible_returnsProductAndIncrementsVisit() {
        Users owner = new Users();
        owner.setId(1L);

        Users viewer = new Users();
        viewer.setId(2L);

        Product product = new Product();
        product.setId(10L);
        product.setUser(owner);
        product.setVisitCount(0L);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(userService.getCurrentUser())
                .thenReturn(viewer);

        when(usersBlockRepository.findByBlockerAndBlocked(any(), any()))
                .thenReturn(Optional.empty());

        when(imageRepository.findByProduct(product))
                .thenReturn(List.of());

        when(favouriteRepository.findFavouriteProductIds(any(), any()))
                .thenReturn(Set.of());

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(new ProductDTO.Response());

        ProductDTO.Response response =
                productService.getProductById(10L);

        assertNotNull(response);
        assertEquals(1, product.getVisitCount());
    }

    @Test
    void getAllProducts_whenUserNotLoggedIn_returnsAvailableProducts() {
        Product product = new Product();
        product.setId(1L);

        when(userService.getCurrentUser())
                .thenThrow(new RuntimeException());

        when(productRepository.findAllByStatus(Status.AVAILABLE))
                .thenReturn(List.of(product));

        when(imageRepository.findAllByProductIn(any()))
                .thenReturn(List.of());

        when(productMapper.toResponse(product))
                .thenReturn(new ProductDTO.Response());

        List<ProductDTO.Response> responses =
                productService.getAllProducts();

        assertEquals(1, responses.size());
    }

    @Test
    void getAllProducts_whenUserLoggedInAndNoBlockedUsers_returnsProducts() {
        Users user = new Users();
        user.setId(1L);

        Product product = new Product();
        product.setId(10L);

        when(userService.getCurrentUser()).thenReturn(user);

        when(usersBlockRepository.findBlockedUserIds(user))
                .thenReturn(Set.of());

        when(usersBlockRepository.findUsersWhoBlockedMeIds(user))
                .thenReturn(Set.of());

        when(productRepository.findAllByStatus(Status.AVAILABLE))
                .thenReturn(List.of(product));

        when(imageRepository.findAllByProductIn(any()))
                .thenReturn(List.of());

        when(favouriteRepository.findFavouriteProductIds(user, List.of(product)))
                .thenReturn(Set.of());

        when(productMapper.toResponse(product))
                .thenReturn(new ProductDTO.Response());

        List<ProductDTO.Response> responses =
                productService.getAllProducts();

        assertEquals(1, responses.size());
    }

    @Test
    void getProductById_whenOwnerViewsProduct_doesNotIncrementVisit() {
        Users owner = new Users();
        owner.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setUser(owner);
        product.setVisitCount(5L);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(userService.getCurrentUser())
                .thenReturn(owner);

        when(usersBlockRepository.findByBlockerAndBlocked(any(), any()))
                .thenReturn(Optional.empty());

        when(imageRepository.findByProduct(product))
                .thenReturn(List.of());

        when(productMapper.toResponse(product))
                .thenReturn(new ProductDTO.Response());

        productService.getProductById(10L);

        assertEquals(5L, product.getVisitCount());
        verify(productRepository, never()).save(product);
    }

}
