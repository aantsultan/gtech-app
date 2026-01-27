package id.task.gtech.dto;

import lombok.Data;

@Data
public class ResponseDto<T> {

    private T data;
    private String error;

    public ResponseDto() {
    }

    public ResponseDto(T data) {
        this.data = data;
    }

    public ResponseDto(T data, String error) {
        this.data = data;
        this.error = error;
    }
}
