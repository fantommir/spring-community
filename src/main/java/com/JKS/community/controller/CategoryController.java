package com.JKS.community.controller;

import com.JKS.community.dto.CategoryDto;
import com.JKS.community.dto.CategoryFormDto;
import com.JKS.community.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryFormDto categoryFormDto) {
        return ResponseEntity.ok(categoryService.create(categoryFormDto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getList() {
        return ResponseEntity.ok(categoryService.getList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CategoryFormDto categoryFormDto) {
        categoryService.update(id, categoryFormDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
