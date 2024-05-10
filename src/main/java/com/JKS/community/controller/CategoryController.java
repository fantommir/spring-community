package com.JKS.community.controller;

import com.JKS.community.dto.CategoryDto;
import com.JKS.community.dto.CategoryFormDto;
import com.JKS.community.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "카테고리 생성")
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryFormDto categoryFormDto) {
        return ResponseEntity.ok(categoryService.create(categoryFormDto));
    }

    @GetMapping
    @Operation(summary = "카테고리 목록 조회")
    public ResponseEntity<List<CategoryDto>> getList() {
        return ResponseEntity.ok(categoryService.getListByDepth(0));
    }

    @GetMapping("/{id}")
    @Operation(summary = "카테고리 조회")
    public ResponseEntity<CategoryDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "카테고리 수정")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CategoryFormDto categoryFormDto) {
        categoryService.update(id, categoryFormDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "카테고리 삭제")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
