package org.sumanta.util;

import java.io.InputStream;
import java.io.ObjectInputStream;

public class DeSerializer {

  public static Object deserialize(InputStream stream) throws Exception {

    ObjectInputStream ois = new ObjectInputStream(stream);
    try {
      return ois.readObject();
    } finally {
      ois.close();
    }
  }

}
