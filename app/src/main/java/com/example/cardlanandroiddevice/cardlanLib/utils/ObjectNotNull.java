//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.cardlanandroiddevice.cardlanLib.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectNotNull {
    public ObjectNotNull() {
    }

    public static boolean notNull(Object object) {
        if(object instanceof String) {
            String sObj = (String)object;
            if(sObj != null && sObj.trim().length() > 0) {
                return true;
            }
        } else if(object instanceof List) {
            List<?> listObj = (List)object;
            if(listObj != null && listObj.size() > 0) {
                return true;
            }
        } else if(object instanceof Map) {
            Map<?, ?> mapObj = (Map)object;
            if(mapObj != null && mapObj.size() > 0) {
                return true;
            }
        } else if(object instanceof Set) {
            Set<?> setObj = (Set)object;
            if(setObj != null && setObj.size() > 0) {
                return true;
            }
        } else if(object instanceof boolean[]) {
            boolean[] objArray = (boolean[])((boolean[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof byte[]) {
            byte[] objArray = (byte[])((byte[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof char[]) {
            char[] objArray = (char[])((char[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof double[]) {
            double[] objArray = (double[])((double[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof float[]) {
            float[] objArray = (float[])((float[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof int[]) {
            int[] objArray = (int[])((int[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof long[]) {
            long[] objArray = (long[])((long[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof short[]) {
            short[] objArray = (short[])((short[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object instanceof String[]) {
            String[] objArray = (String[])((String[])object);
            if(objArray != null && objArray.length > 0) {
                return true;
            }
        } else if(object != null) {
            return true;
        }

        return false;
    }
}
