package jzq.server.netty.protocol;

import io.netty.buffer.ByteBuf;

/**
 * 协议抽象接口
 */
public interface UART {

    /**
     * 得到所有数据
     *
     * @return byte数组
     */
    byte[] getData();

    /**
     * 设置数据
     *
     * @param data byte数组
     */
    void setData(ByteBuf data);
//    void setData(byte[] data);


    /**
     * 启动解析
     */
    void parse();

    /**
     * 设置帧内容 为除开枕头帧尾数据长度等数据之外的实际有用的数据
     * @param concent
     */
    void setConcent(byte[] concent);

}
