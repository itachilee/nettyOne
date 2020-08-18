package jzq.server.netty.protocol;

import io.netty.buffer.ByteBuf;

/**
 *
 */

public abstract class AbstractUART implements UART {


    /**
     * 协议最小长度
     */
    public final static int MIN_LENGTH = 18;
    /**
     * 协议最大长度
     */
    public final static int MAX_LENGTH = 215;
    /**
     * 帧头与帧尾的长度
     */
    public final static int FLASH_LENGTH = 2;
    protected byte headData;
    protected int length;
    /**
     * 命令序号
     */
    protected int sendId;
    /**
     * 目标地址
     */
    protected int destAddr;
    /**
     * 源地址
     */
    protected int srcAddr;

    /**
     * 标识码
     */
    protected byte cmd;
    /**
     * 一条完整的数据包
     */
    protected byte[] data;
    /**
     * 除开帧头帧尾等描述数据之外的有用的数据
     * 具体数据内容查看协议
     */
    protected byte[] concent;
    /**
     * 两个字节的校验码
     */
    protected short check;
    protected byte dataEnd;

    public AbstractUART() {
    }


    /**
     * 用于发送协议初始化
     *
     * @param mark 识别码
     * @param type 操作码
     */
    public AbstractUART(byte mark, char type) {
//        this.mark = mark;
////        this.type = type;
//
//        ProtocolConfig protocolConfig = ConfigContext.getInstace().getProtocolConfig();
//        Integer size = protocolConfig.getReceiveMap().get(mark) + 2;
//        byte[] bytes = new byte[size];
//        for (int i = 0; i < size; i++) {
//            bytes[i] = 0x20;
//        }
//        this.setData(bytes);
//
//        //填充byte数组
//        data[0] = this.mark; // 命令代码 controller 分发
//
//        data[size - 1] = (byte) 0xEE; // 终止符
    }


    protected AbstractUART(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public void setData(ByteBuf data) {
        if (data.readableBytes() < MIN_LENGTH) {
            return;
//            setHeadData(data[0]);
        }
        int flashLength =FLASH_LENGTH+data.getByte(1);

        int readableBytes =data.readableBytes();
        if (flashLength <= readableBytes) {
            this.headData = data.readByte();
            this.length = data.readByte() & 0xFF;
            this.sendId = data.readInt();
            this.destAddr = data.readInt();
            this.srcAddr = data.readInt();
            this.cmd = data.readByte();
            byte[] dataBytes = new byte[ length - 16];
            data.readBytes(dataBytes);
            this.data = dataBytes;
            byte[] checkCrcBytes = new byte[2];
            data.readBytes(checkCrcBytes);
            this.dataEnd = data.readByte();
        }

    }
//    @Override
//    public void setData(byte[] data) {
//        this.data = data;
//        if (data.length > MIN_LENGTH) {
//
////            setHeadData(data[0]);
//            this.headData = data[0];
//        }
//    }


    /**
     * 设置帧内容 为除开枕头帧尾数据长度等数据之外的实际有用的数据
     *
     * @param concent
     */
    @Override
    public void setConcent(byte[] concent) {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte item : this.data) {
            Integer valueOf = Integer.valueOf(item);
            sb.append(Integer.toHexString(valueOf) + "\t");
        }
        return sb.toString();
    }


    /**
     * 将byte数组插入到指定下标
     *
     * @param bytes 插入数组
     * @param idx   插下标
     * @return 插入结束下标
     */
    protected int insertArr(byte[] bytes, int idx) {
        if (bytes != null) {
            for (int i = 0; i < bytes.length; i++) {
                data[i + idx] = bytes[i];
            }
            return idx + bytes.length;
        }
        return idx;
    }

}
