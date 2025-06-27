package rongsokan.indah.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rongsokan.indah.entities.Modal;
import rongsokan.indah.services.ModalService;

@RestController
@RequestMapping("/api/modal")
public class ModalController {

    @Autowired
    private ModalService modalService;

    @PostMapping
    public Modal saveModal(@RequestBody Modal modal) {
        return modalService.saveModal(modal);
    }

    @GetMapping
    public Page<Modal> getModal(
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return modalService.getModal(cari, pageable);
    }

    @PutMapping
    public Modal updateModal(@RequestBody Modal modal) {
        return modalService.updateModal(modal);
    }

    @DeleteMapping("/{id}")
    public void deleteModal(@PathVariable UUID id) {
        modalService.deleteModal(id);
    }
}