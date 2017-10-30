package com.moonabyss.l2;

import java.awt.*;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by uncle on 21.04.2016.
 */
public class Main {

    static int x1;
    static int y1;
    static int x2;
    static int y2;

    public static void main(String[] args) {

        if (args.length < 6) {
            System.out.println("Недостаточно аргументов");
            System.exit(-1);
        }

        x1 = Integer.parseInt(args[2]);
        y1 = Integer.parseInt(args[3]);
        x2 = Integer.parseInt(args[4]);
        y2 = Integer.parseInt(args[5]);

        HashMap<Point, byte[]> sourceFile= new HashMap<>();
        int start, end = 0;

        try(RandomAccessFile raf = new RandomAccessFile(args[0], "r"); FileChannel fc = raf.getChannel()) {
            MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(18);
            for (int i = 0; i < 0x10000; i++) {
                int x = i / 256;
                int y = i % 256;
                /*if (x == 6 && y == 53)
                    System.exit(0);*/
                //System.out.printf("Block X: %d, Y: %d ",x, y);
                start = buffer.position();
                short t = buffer.getShort();
                switch (t) {
                    case 0x0000:  {
                        short flat1 = buffer.getShort();
                        short flat2 = buffer.getShort();
                        end = buffer.position();
                        byte[] block = new byte[end - start];
                        buffer.position(start);
                        for (int b = 0; b < block.length; b++) {
                            block[b] = buffer.get();
                        }
                        sourceFile.put(new Point(x, y), block);
                        //System.out.print("Block type: FLAT ");
                        //System.out.printf("Z: %d %d%n", flat1, flat2);
                        break;
                    }
                    case 0x0040:  {
                        //System.out.printf("Block type: COMPLEX%n");
                        for (int j = 0; j < 64; j++) {
                            int cellX = j / 8;
                            int cellY = j % 8;
                            short complex = buffer.getShort();
                            //System.out.printf("\tCell X: %d, Y: %d, Z: %d%n",cellX,cellY,complex);
                        }
                        end = buffer.position();
                        byte[] block = new byte[end - start];
                        buffer.position(start);
                        for (int b = 0; b < block.length; b++) {
                            block[b] = buffer.get();
                        }
                        sourceFile.put(new Point(x, y), block);
                        break;
                    }
                    default: {
                        //System.out.printf("Block type: MULTILAYER%n");
                        for (int j = 0; j < 64; j++) {
                            int cellX = j / 8;
                            int cellY = j % 8;
                            short layers = buffer.getShort();
                            for (int k = --layers; k >= 0; k--) {
                                short multi = buffer.getShort();
                                //System.out.printf("\tCell X: %d, Y: %d, Z: %d, Layer: %d%n", cellX, cellY, (short)(multi&0xFFF0)>>1, k);
                            }
                        }
                    }
                    end = buffer.position();
                    byte[] block = new byte[end - start];
                    buffer.position(start);
                    for (int b = 0; b < block.length; b++) {
                        block[b] = buffer.get();
                    }
                    sourceFile.put(new Point(x, y), block);
                }





            }
            //System.out.println(buffer.position());
            /*System.out.println(sourceFile.size());
            for (int i = 0; i < 0x10000; i++) {
                int x = i / 256;
                int y = i % 256;
                //System.out.println(Arrays.toString(sourceFile.get(new Point(x, y))));
                if (isInBound(x, y)) {
                    System.out.println(x+","+y);
                }
            }*/

        } catch(Throwable e) {
            e.printStackTrace();
        }


        try(RandomAccessFile raf = new RandomAccessFile(args[1], "r"); FileChannel fc = raf.getChannel()) {
            MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(18);
            for (int i = 0; i < 0x10000; i++) {
                int x = i / 256;
                int y = i % 256;
                /*if (x == 6 && y == 53)
                    System.exit(0);*/
                start = buffer.position();
                short t = buffer.getShort();
                switch (t) {
                    case 0x0000:  {
                        short flat1 = buffer.getShort();
                        short flat2 = buffer.getShort();
                        if (isInBound(x, y)) {
                            end = buffer.position();
                            byte[] block = new byte[end - start];
                            buffer.position(start);
                            for (int b = 0; b < block.length; b++) {
                                block[b] = buffer.get();
                            }
                            sourceFile.put(new Point(x, y), block);
                        }
                        break;
                    }
                    case 0x0040:  {
                        for (int j = 0; j < 64; j++) {
                            int cellX = j / 8;
                            int cellY = j % 8;
                            short complex = buffer.getShort();
                            //System.out.printf("\tCell X: %d, Y: %d, Z: %d%n",cellX,cellY,complex);
                        }
                        if (isInBound(x, y)) {
                            end = buffer.position();
                            byte[] block = new byte[end - start];
                            buffer.position(start);
                            for (int b = 0; b < block.length; b++) {
                                block[b] = buffer.get();
                            }
                            sourceFile.put(new Point(x, y), block);
                        }
                        break;
                    }
                    default: {
                        for (int j = 0; j < 64; j++) {
                            int cellX = j / 8;
                            int cellY = j % 8;
                            short layers = buffer.getShort();
                            for (int k = --layers; k >= 0; k--) {
                                short multi = buffer.getShort();
                            }
                        }
                    }
                    if (isInBound(x, y)) {
                        end = buffer.position();
                        byte[] block = new byte[end - start];
                        buffer.position(start);
                        for (int b = 0; b < block.length; b++) {
                            block[b] = buffer.get();
                        }
                        sourceFile.put(new Point(x, y), block);
                    }
                }

            }

        } catch(Throwable e) {
            e.printStackTrace();
        }

        try(RandomAccessFile raf = new RandomAccessFile(args[1]+"-", "rw"); FileChannel fc = raf.getChannel()) {

            int size = 0;
            for (int i = 0; i < 0x10000; i++) {
                int x = i / 256;
                int y = i % 256;
                size += sourceFile.get(new Point(x, y)).length;
            }
            ByteBuffer buffer = ByteBuffer.allocate(size + 18);
            buffer.position(18);
            for (int i = 0; i < 0x10000; i++) {
                int x = i / 256;
                int y = i % 256;
                byte[] tempBlock = sourceFile.get(new Point(x, y));
                for (byte b: tempBlock) {
                    buffer.put(b);
                }
            }
            //buffer.flip();
            buffer.position(0);
            fc.write(buffer);

        } catch(Throwable e) {
            e.printStackTrace();
        }

        try {
            CheckGeo.calculateHead(args[1]+"-");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static boolean isInBound (int x, int y) {
        return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
    }

}
