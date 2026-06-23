    package com.rahul.chitfund_backend.controller;

    import com.rahul.chitfund_backend.entity.ChitGroup;
    import com.rahul.chitfund_backend.service.ChitGroupService;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/chits")
    public class ChitGroupController {

        private final ChitGroupService service;

        public ChitGroupController(ChitGroupService service) {
            this.service = service;
        }

        @PostMapping
        public ChitGroup create(@RequestBody ChitGroup chitGroup) {
            return service.createChitGroup(chitGroup);
        }

        @GetMapping
        public List<ChitGroup> getAll() {
            return service.getAllChitGroups();
        }
    }
