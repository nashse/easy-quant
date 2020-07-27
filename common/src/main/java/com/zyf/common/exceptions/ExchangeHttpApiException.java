package com.zyf.common.exceptions;

import lombok.Data;

/**
 * Description:Exchange Http Api Exception
 * @author yuanfeng.z
 * @date 2019/6/25 10:27
 * @see RuntimeException
 */
@Data
public class ExchangeHttpApiException extends RuntimeException {

    private static final long serialVersionUID = -7864604160297181941L;

    /** 错误码 */
    protected final ErrorCode errorCode;

    /**
     * 这个是和谐一些不必要的地方,冗余的字段
     * 尽量不要用
     */
    private String code;

    /**
     * 无参默认构造UNSPECIFIED
     */
    public ExchangeHttpApiException() {
        super(EHAErrorCodeEnum.UNSPECIFIED.getDescription());
        this.errorCode = EHAErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 指定错误码构造通用异常
     * @param errorCode 错误码
     */
    public ExchangeHttpApiException(final ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    /**
     * 指定详细描述构造通用异常
     * @param detailedMessage 详细描述
     */
    public ExchangeHttpApiException(final String detailedMessage) {
        super(detailedMessage);
        this.errorCode = EHAErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 指定导火索构造通用异常
     * @param t 导火索
     */
    public ExchangeHttpApiException(final Throwable t) {
        super(t);
        this.errorCode = EHAErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param detailedMessage 详细描述
     */
    public ExchangeHttpApiException(final ErrorCode errorCode, final String detailedMessage) {
        super(detailedMessage);
        this.errorCode = errorCode;
    }

    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param t 导火索
     */
    public ExchangeHttpApiException(final ErrorCode errorCode, final Throwable t) {
        super(errorCode.getDescription(), t);
        this.errorCode = errorCode;
    }

    /**
     * 构造通用异常
     * @param detailedMessage 详细描述
     * @param t 导火索
     */
    public ExchangeHttpApiException(final String detailedMessage, final Throwable t) {
        super(detailedMessage, t);
        this.errorCode = EHAErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param detailedMessage 详细描述
     * @param t 导火索
     */
    public ExchangeHttpApiException(final ErrorCode errorCode, final String detailedMessage,
                        final Throwable t) {
        super(detailedMessage, t);
        this.errorCode = errorCode;
    }

    /**
     * Getter method for property <tt>errorCode</tt>.
     *
     * @return property value of errorCode
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }


    public static void main(String[] args) {
        String s=new String("quantity_low\n")
                //.replaceAll("String symbol","\"1200\",\"\"")
                .toUpperCase();
        System.out.print(s);
    }
}