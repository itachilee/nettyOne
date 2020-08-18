package jzq.server.netty.codec;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import jzq.configuration.ConfigContext;
import jzq.server.netty.protocol.AbstractUART;
import jzq.server.netty.protocol.ProtocolFactory;
import jzq.util.BytesUtils;
import jzq.util.CRCUtils;

import java.util.List;

/**
 * 基础发送数据包
 * 基本的帧结构
 * +----------+----------+--------------------------------------------------------
 * |  大小    |  固定值   |  摘要
 * +----------+----------+--------------------------------------------------------
 * | 1 bytes  | 0x68     |  帧起始符
 * | 1 bytes  |         |  长度
 * | 4 bytes  |         |  命令序号
 * | 4 bytes  |         |  目标地址
 * | 4 bytes  |         | 源地址
 * <p>
 * | 0~200byte |          |  内容
 * | 2 bytes  |          |  校验码 CRC 校验
 * | 1 bytes  | 0xEE     |  帧结束符
 * +----------+----------+--------------------------------------------------------
 * 加密范围:帧类型+确认码+内容长度+内容.
 */
public class UARTDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= AbstractUART.MIN_LENGTH) {

            //防止消息过大,客户端攻击

            if (in.readableBytes() > AbstractUART.MAX_LENGTH) {
                // readerIndex指针后移
                in.skipBytes(in.readableBytes());

            }


            int startReader;

            ConfigContext instance = ConfigContext.getInstance();

            byte mark;
            int length;
            while (true) {
                startReader = in.readerIndex();
                in.markReaderIndex();
                mark = in.readByte();
                int head = mark & 0xff;
                // 帧长度
                length = in.readByte();
                if (isSupport(instance, head)) {
                    break;
                }
                in.resetReaderIndex();
                in.readByte();

                if (in.readableBytes() < (length - 1)) {
                    return;
                }
            }


            in.readerIndex(startReader);
            if (in.readableBytes() < length) {
                // 等待数据到齐
                return;
            }
            int dataLength = in.getByte(startReader + 1);
            // 转换为无符号 4位的整数
            int cmd = in.getByte(startReader + 14) & 0xFF;
            ByteBuf newByteBuf = in.slice(startReader, startReader + dataLength + 2);
//            newByteBuf.readerIndex(startReader);
//            byte head =in.readByte();
//            byte newLen =in.readByte();
//            int sendId =in.readInt();
//            int destAddr =in.readInt();
//            int srcAddr=in.readInt();
//            int cmd =in.readByte()&0xFF;
//            int idx =in.readerIndex()+1;
//            int copyLen=length-16;
//            byte[] data = new byte[copyLen];
//            in.readBytes(data);
            in.skipBytes(dataLength + 1);

            Object uart = ProtocolFactory.createUART(cmd, newByteBuf);
//            Object uart = ProtocolFactory.createUART(cmd, data);

            out.add(uart);

        }
    }

    public static boolean checkCRC(byte[] content) {
        short crc_result = CRCUtils.getCRC(content, 0, content.length - 2);
        byte[] crc_raw = new byte[2];
        System.arraycopy(content, content.length - 2, crc_raw, 0, 2);
        short crc_short = BytesUtils.byteArrayToShort(crc_raw);
        return crc_result == crc_short;
    }

    /**
     * 判断是否支持协议
     *
     * @param instance
     * @param mark
     * @return
     */
    private boolean isSupport(ConfigContext instance, int mark) {
//        return true;
        return instance.getProtocolConfig().getReceiveContainer().contains(mark);
    }
}
