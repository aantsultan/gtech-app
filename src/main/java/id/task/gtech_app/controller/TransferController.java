package id.task.gtech_app.controller;

import id.task.gtech_app.dto.TransferDto;
import id.task.gtech_app.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("transfer")
    public ResponseEntity<String> post(@RequestBody TransferDto dto) {
        return ResponseEntity.ok(transferService.post(dto));
    }

}
