package com.thaihoc.miniinsta.service;

public interface FileService {
  String uploadImage(String base64);

  String downloadImage(String fileName);
}
