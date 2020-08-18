package jzq.server.netty.protocol.receive;

import jzq.server.netty.NettyContainer;
import jzq.server.netty.protocol.AbstractUART;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;


public class FaultUART extends AbstractUART {

    //故障时间
    private Date faultTime;

    //超时时长
    private String eq1;
    //主道口人员
    private String checkPreson;
    //副道口人员
    private String reviewPerson;

    public FaultUART() {
    }

    public FaultUART(byte[] data) {
        super(data);
    }



    public void main(String[] args) {
        FaultUART uart = new FaultUART(new byte[]{65, 0x30, 0x31, 0x38, 0x30, 0x35, 0x32, 0x32, 0x30, 0x34, 0x32, 0x34, 0x34, 0x31,0x31,0x31,0x34,0x31,0x30,0x31,0x34,0x31,0x30, 0x32});
        uart.parse();
//        System.out.println(uart.getFaultTime());
    }



    @Override
    public void parse() {
        System.out.println("Data:" + data);
        System.out.println("headData:" + headData);

    }




}
