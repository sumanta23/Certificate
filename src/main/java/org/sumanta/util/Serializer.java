package org.sumanta.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Serializer {

  public static byte[] serialize(Object obj) {
    byte[] objectAsBytes = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);
      objectAsBytes = baos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      return objectAsBytes;
    }
  }

}
