package com.thaihoc.miniinsta.dto.message;

import lombok.Data;

@Data
public class SendMessageInput {
	private String receiver;
	private String content;
}
