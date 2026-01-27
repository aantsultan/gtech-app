package id.task.gtech.controller;

import id.task.gtech.dto.ResponseDto;
import id.task.gtech.dto.TransferDto;
import id.task.gtech.service.TransferService;
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
    public ResponseEntity<ResponseDto<String>> post(@RequestBody TransferDto dto) {
        return ResponseEntity.ok(new ResponseDto<>(transferService.post(dto)));
    }

}
