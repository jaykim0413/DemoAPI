package com.demo.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponse {
    private String name;
    private String url;
    private String type;
    private long size;
}
