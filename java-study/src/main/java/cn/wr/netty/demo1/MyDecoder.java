package cn.wr.netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class MyDecoder extends ByteToMessageDecoder {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(MyDecoder.class);
    private static String clientIpAllowed = "117.74.136.117";  //该编码器只试用于指定客户端传入的数据，因为协议是根据客户端数据类型定义的

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        System.out.println("...............客户端连接服务器...............");
        String client = ctx.channel().remoteAddress().toString();
        System.out.println("客户端IP: " + client);
        System.out.println("连接时间："+ sdf.format(new Date()));

        //判断IP合法性
        if (StringUtils.isNotBlank(client) && client.contains(clientIpAllowed)) {
            System.out.println("...............客户端IP合法，开始数据解析 ...............");
            //简单的解释下，ByteBuf拿到的就是数据包，tcp头已被netty处理，直接根据协议对ByteBuf内容进行解析即可
            //解析时需根据协议定义的数据类型进行解析，如：
            //unsigned char headFlag[3]; 定义了headFlag这个字段是长度为3的无符号char类型数组，每个char占一个字节，
            // 所以应该读取3位：ByteBuf headFlag = buf.readBytes(3);
            // 根据协议可知，headFlag存储的是 headFlag[0]=0x2E headFlag[1]=0x81 headFlag[2]=0xD016进制数据，所以需转化为16进制显示：
            // dataMap.put("headFlag", ByteBufUtil.hexDump(headFlag))。
            //unsigned char packetID; 定义了packetID是一个无符号的char类型字符，直接使用buf.readUnsignedByte()进行解析。
            //short retcode; 定义了retcode是一个short类型数值，使用buf.readShortLE()解析，注意：字符类型大于1byte的，需要注意大小端，使用LE结尾的方法进行解析，如readShortLE()，如果使用LE结尾方法解析的值不正确，可以再使用不带LE的方法进行解析,如：readShort()。
            // 注意 ByteBuf  的read 操作是会改变索引 如果读取和规定格式不一样  是会读串数据的.

            //开始解析
            //定义一个map接收数据
            LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
            //定义个StringBuffer用于字符串拼接
            StringBuffer sb = new StringBuffer();
            //数据接收时间
            dataMap.put("dataTime",sdf.format(new Date()));

            //按照规定的数据协议读取数据
            //------------------包头-------------------
            //头部标识
            ByteBuf headFlag = buf.readBytes(3);
            dataMap.put("headFlag", ByteBufUtil.hexDump(headFlag));

            //命令
            ByteBuf command = buf.readBytes(2);
            dataMap.put("command", ByteBufUtil.hexDump(command));

            //序列号
            dataMap.put("packetID", buf.readUnsignedByte());

            //返回值
            dataMap.put("retcode", buf.readShort());

            //本次数据包的长度
            int DataLen = buf.readUnsignedShort();
            dataMap.put("DataLen", DataLen);

            //--------------数据包---------------
            if (DataLen > 0) {
                //设备号
                ByteBuf DeviceNo = buf.readBytes(12);
                dataMap.put("DeviceNo", convertByteBufToString(DeviceNo));

                //车牌号
                ByteBuf CarNum = buf.readBytes(16);
                dataMap.put("CarNum", convertByteBufToString(CarNum));

                //当前司机编号
                ByteBuf DriverID = buf.readBytes(16);
                dataMap.put("DriverID", convertByteBufToString(DriverID));

                //经度
                sb = new StringBuffer();
                for (int i = 0; i < 4; i++) {
                    if (i==0) {
                        sb.append(String.valueOf(buf.readUnsignedByte()) + ".");
                    } else {
                        sb.append(String.valueOf(buf.readUnsignedByte()));
                    }
                }
                dataMap.put("x", sb.toString());

                //纬度
                sb = new StringBuffer();
                for (int i = 0; i < 4; i++) {
                    if (i==0) {
                        sb.append(String.valueOf(buf.readUnsignedByte()) + ".");
                    } else {
                        sb.append(String.valueOf(buf.readUnsignedByte()));
                    }
                }
                dataMap.put("y", sb.toString());

                //当前的速度
                dataMap.put("speed", buf.readUnsignedByte());

                //当前司机人脸识别状态
                dataMap.put("DrverAuth", buf.readUnsignedByte());

                //GPS模块状态
                dataMap.put("gpsinvalid", buf.readUnsignedByte());

                //越界报警
                dataMap.put("overZoneWarn", buf.readUnsignedByte());

                //超速报警
                dataMap.put("overSpeedWarn", buf.readUnsignedByte());

                //超重报警
                dataMap.put("overWeightWarn", buf.readUnsignedByte());

                //胎温\胎压\轴温报警
                dataMap.put("TryeWarn", buf.readUnsignedByte());

                //ADAS和DSM报警
                dataMap.put("DriverStatusWarn", buf.readUnsignedByte());

                //发动机转速
                dataMap.put("rpm", buf.readUnsignedShort());

                //卫星定位角度
                ByteBuf direc = buf.readBytes(10);
                dataMap.put("direc", convertByteBufToString(direc));

                //司机连续开车的时间分钟
                dataMap.put("ContinueDrvTime", buf.readUnsignedShort());

                //后面的数据长度
                int DataLen2 = buf.readUnsignedShort();
                dataMap.put("DataLen2", DataLen2);

                if(DataLen2>0){
                    //数据类型
                    long DataType = buf.readUnsignedInt();
                    dataMap.put("DataType", DataType);
                    //后面的数据长度
                    long DataLen3 = buf.readUnsignedInt();
                    dataMap.put("DataLen3", DataLen3);

                    if (DataLen3>0) {
                        //1.温度数据
                        if (DataType == 1) {
                            //车厢的温度 冷链车专用 精确到0.01摄氏
                            dataMap.put("BoxTemp", buf.readInt());
                            //车厢的湿度 -冷链车专用精确到0.01%
                            dataMap.put("humidity", buf.readInt());
                            //违规放置物品报价
                            dataMap.put("ObjectWarn", buf.readUnsignedInt());
                            //
                            dataMap.put("resv", buf.readUnsignedInt());
                        }
                        //2.胎温胎压数据
                        else if (DataType == 2) {
                            //轮胎报警类型
                            ByteBuf TryeWarType = buf.readBytes(32);
                            dataMap.put("TryeWarType", convertByteBufToString(TryeWarType));

                            //轮胎编号
                            ByteBuf TryeID = buf.readBytes(32);
                            dataMap.put("TryeID", convertByteBufToString(TryeID));

                            //轮胎温度或者压力的值
                            sb = new StringBuffer();
                            for (int i = 0; i < 32; i++) {
                                sb.append(buf.readUnsignedShort());
                            }
                            dataMap.put("TryeValue", sb.toString());

                            //车轴编号
                            ByteBuf AxiesID = buf.readBytes(16);
                            dataMap.put("AxiesID", convertByteBufToString(AxiesID));

                            //车轴温度
                            sb = new StringBuffer();
                            for (int i = 0; i < 32; i++) {
                                sb.append(buf.readUnsignedShort());
                            }
                            dataMap.put("AxiesValue", sb.toString());

                        }
                        //3.重量数据
                        else if (DataType == 3) {
                            //当前的起吊的重量，精确到0.1
                            dataMap.put("weight", buf.readUnsignedInt());
                        }
                        //4.ADAS和DSM
                        else if (DataType == 4) {
                            //报警类型
                            dataMap.put("WarnType", buf.readUnsignedInt());
                        }
                        //5.照片视频数据
                        else if (DataType == 5) {
                            //照片或者视频文件名称
                            ByteBuf FileName = buf.readBytes(20);
                            dataMap.put("FileName", convertByteBufToString(FileName));

                            //照片或者视频的下载链接
                            ByteBuf url = buf.readBytes(256);
                            dataMap.put("url", convertByteBufToString(url));
                        }
                    }
                }

                //--------------包尾---------------
                //校验和，包含包头和数据
                dataMap.put("CRC", buf.readUnsignedShort());
            }

            //存到map中  ，传递到下一个业务处理的handler
            out.add(dataMap);
        } else {
            System.out.println("...............服务端暂不接收该IP数据 ...............");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("...............数据解析异常 ...............");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * ByteBuf转String
     * @param buf
     * @return
     * @throws Exception
     */
    public String convertByteBufToString(ByteBuf buf) throws Exception {
        String str;
        if(buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

}
