package com.JKS.community.service;

import com.JKS.community.dto.CategoryDto;
import com.JKS.community.dto.CategoryFormDto;
import com.JKS.community.entity.Category;
import com.JKS.community.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    private Category category1;
    private Category category2;
    private Category tab1;
    private Category tab2;
    private Category tab3;

    private CategoryFormDto category1FormDto;
    private CategoryFormDto category2FormDto;
    private CategoryFormDto tab1FormDto;
    private CategoryFormDto tab2FormDto;
    private CategoryFormDto tab3FormDto;


    @BeforeEach
    void setUp() {
        category1FormDto = new CategoryFormDto("category1", true, null);
        category2FormDto = new CategoryFormDto("category2", true, null);
        category1 = Category.of(category1FormDto.getName(), null, category1FormDto.getEnabled());
        ReflectionTestUtils.setField(category1, "id", 1L);
        category2 = Category.of(category2FormDto.getName(), null, true);
        ReflectionTestUtils.setField(category2, "id", 2L);

        tab1FormDto = new CategoryFormDto("tab1", true, category1.getId());
        tab2FormDto = new CategoryFormDto("tab2", false, category1.getId());
        tab3FormDto = new CategoryFormDto("tab3", true, category2.getId());
        tab1 = Category.of(tab1FormDto.getName(), category1, tab1FormDto.getEnabled());
        ReflectionTestUtils.setField(tab1, "id", 3L);
        tab2 = Category.of(tab2FormDto.getName(), category1, tab2FormDto.getEnabled());
        ReflectionTestUtils.setField(tab2, "id", 4L);
        tab3 = Category.of(tab3FormDto.getName(), category2, tab3FormDto.getEnabled());
        ReflectionTestUtils.setField(tab3, "id", 5L);
    }

    @Test
    @DisplayName("카테고리, 탭 생성 - 성공")
    void create() {
        // Given
        when(categoryRepository.findById(category1.getId())).thenReturn(Optional.of(category1));

        // When
        CategoryDto category1Dto = categoryService.create(category1FormDto);
        CategoryDto tab1Dto = categoryService.create(tab1FormDto);

        // Then
        assertNotNull(category1Dto);
        assertEquals(category1FormDto.getName(), category1Dto.getName());

        assertNotNull(tab1Dto);
        assertEquals(tab1FormDto.getName(), tab1Dto.getName());
        assertEquals(1L, tab1Dto.getParentId());

        verify(categoryRepository, times(1)).findById(tab1Dto.getParentId());
        verify(categoryRepository, times(2)).save(any(Category.class));
    }


    @Test
    @DisplayName("depth로 목록 조회 - 성공")
    void getListByDepth() {
        when(categoryRepository.findAllByDepth(0)).thenReturn(
                List.of(category1, category2)
        );
        when(categoryRepository.findAllByDepth(1)).thenReturn(
                List.of(tab1, tab2, tab3)
        );

        List<CategoryDto> categoryDtoList = categoryService.getListByDepth(0);
        List<CategoryDto> tabDtoList = categoryService.getListByDepth(1);

        assertNotNull(categoryDtoList);
        assertEquals(2, categoryDtoList.size());
        assertEquals(category1.getName(), categoryDtoList.get(0).getName());
        assertEquals(category2.getName(), categoryDtoList.get(1).getName());

        assertNotNull(tabDtoList);
        assertEquals(3, tabDtoList.size());
        assertEquals(tab1.getName(), tabDtoList.get(0).getName());
        assertEquals(tab2.getName(), tabDtoList.get(1).getName());
        assertEquals(tab3.getName(), tabDtoList.get(2).getName());

        verify(categoryRepository, times(2)).findAllByDepth(anyInt());
    }

    @Test
    @DisplayName("카테고리 조회 - 성공")
    void get() {
        when(categoryRepository.findById(category1.getId())).thenReturn(Optional.of(category1));

        CategoryDto categoryDto = categoryService.get(category1.getId());

        assertNotNull(categoryDto);
        assertEquals(category1.getName(), categoryDto.getName());

        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void update() {
        // Given
        when(categoryRepository.findById(tab1.getId())).thenReturn(Optional.of(tab1));
        when(categoryRepository.findById(category2.getId())).thenReturn(Optional.of(category2));

        // When
        categoryService.update(tab1.getId(), tab3FormDto);

        // Then
        assertEquals(tab3FormDto.getName(), tab1.getName());
        assertEquals(tab3FormDto.getEnabled(), tab1.getEnabled());
        assertEquals(category2.getId(), tab1.getParent().getId());

        verify(categoryRepository, times(1)).findById(tab1.getId());
        verify(categoryRepository, times(1)).findById(category2.getId());
    }


    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void delete() {
        // Given
        when(categoryRepository.findById(tab1.getId())).thenReturn(Optional.of(tab1));

        // When
        categoryService.delete(tab1.getId());

        // Then
        verify(categoryRepository, times(1)).findById(tab1.getId());
        verify(categoryRepository, times(1)).delete(tab1);
    }
}