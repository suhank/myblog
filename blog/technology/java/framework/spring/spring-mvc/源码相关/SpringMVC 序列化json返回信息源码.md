# SpringMVC 序列化json返回信息源码

位置:org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor#writeWithMessageConverters(T, org.springframework.core.MethodParameter, org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.server.ServletServerHttpResponse)



```java
	/**
	 * Writes the given return type to the given output message.
	 * @param value the value to write to the output message
	 * @param returnType the type of the value
	 * @param inputMessage the input messages. Used to inspect the {@code Accept} header.
	 * @param outputMessage the output message to write to
	 * @throws IOException thrown in case of I/O errors
	 * @throws HttpMediaTypeNotAcceptableException thrown when the conditions indicated
	 * by the {@code Accept} header on the request cannot be met by the message converters
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected <T> void writeWithMessageConverters(@Nullable T value, MethodParameter returnType,
			ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage)
			throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

	    ....

		if (selectedMediaType != null) {
			selectedMediaType = selectedMediaType.removeQualityValue();
            //遍历所有HttpMessageConverter 
			for (HttpMessageConverter<?> converter : this.messageConverters) {
				GenericHttpMessageConverter genericConverter = (converter instanceof GenericHttpMessageConverter ?
						(GenericHttpMessageConverter<?>) converter : null);
				//判断是否可以支持json
                if (genericConverter != null ?
						((GenericHttpMessageConverter) converter).canWrite(declaredType, valueType, selectedMediaType) :
						converter.canWrite(valueType, selectedMediaType)) {
					outputValue = getAdvice().beforeBodyWrite(outputValue, returnType, selectedMediaType,
							(Class<? extends HttpMessageConverter<?>>) converter.getClass(),
							inputMessage, outputMessage);
					if (outputValue != null) {
						addContentDispositionHeader(inputMessage, outputMessage);
						if (genericConverter != null) {
							genericConverter.write(outputValue, declaredType, selectedMediaType, outputMessage);
						}
						else {
							((HttpMessageConverter) converter).write(outputValue, selectedMediaType, outputMessage);
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Written [" + outputValue + "] as \"" + selectedMediaType +
									"\" using [" + converter + "]");
						}
					}
					return;
				}
			}
		}

		if (outputValue != null) {
			throw new HttpMediaTypeNotAcceptableException(this.allSupportedMediaTypes);
		}
	}
```



