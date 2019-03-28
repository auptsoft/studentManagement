package com.example.andrewoshodin.fingerprintregister.models;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Andrew Oshodin on 9/4/2018.
 */

public class TemplateIdManager {

    public static String insertTemplateId(Context context, String matNumber, String templateId) {
        return new TemplateId(0, matNumber, templateId).insert(context);
    }

    public static int deleteTemplateId(Context context, String templateId) {
        return new TemplateId(0, "", templateId).delete(context);
    }

    public static int deleteWithMatNo(Context context, String matNo) {
        return new TemplateId(0, matNo, "").deleteWithMatNo(context);
    }

    public static String getTemplateId(Context context, String matNumber) {
        TemplateId templateId = new TemplateId(0, matNumber, "").getWithMatNo(context);

        if (templateId != null) {
            return templateId.getTemplateId();
        }
        return null;
    }

    public static String getMatNo(Context context, String templateId) {
        TemplateId templateIdObject = new TemplateId(0, "", templateId).get(context);
        if (templateIdObject != null) {
            return templateIdObject.getMatNumber();
        }
        return null;
    }

    public static ArrayList<TemplateId> getAllTemplate(Context context) {
        return new TemplateId(0, "", "").getAll(context);
    }

    public static int updateWithMatNo(Context context, String matNo, String templateId) {
        return new TemplateId(0, matNo, templateId).updateWithMatNo(context);
    }
    public static int updateWithTemplateId(Context context, String matNo, String templateId) {
        return new TemplateId(0, matNo, templateId).updateWithTemplateId(context);
    }

    @Nullable
    public static String getMinTemplateIdSlot(Context context) {
        ArrayList<TemplateId> templateIds = getAllTemplate(context);
        if (templateIds.size() > 0) {
            ArrayList<String> templateIdsString = new ArrayList<>();
            for (TemplateId templateId : templateIds) {
                templateIdsString.add(templateId.getTemplateId());
            }

            ArrayList<Integer> usedSlots = new ArrayList<>();
            for(String s : templateIdsString) {
                try {
                    usedSlots.add(Integer.valueOf(s));
                } catch (Exception e) {
                    continue;
                }
            }

            ArrayList<Integer> availableSlots = new ArrayList<>();
            for (int slot=0; slot<1000; slot++) {
                if(!usedSlots.contains(slot)) {
                    availableSlots.add(slot);
                }
            }

            if (availableSlots.size()>0) {
                return appendZeros(getMin(availableSlots));
            } else return null;
        }
        return "0";
    }

    static String appendZeros(int no) {
        if (no<10) return "00"+no;
        else if (no<100) return  "0"+no;
        else return ""+no;
    }

    static int getMin(ArrayList<Integer> list) {
        int min = Integer.MAX_VALUE;
        for (int i : list) {
            if (i<min) min = i;
        }
        return min;
    }

    public static class TemplateId extends Content {
        private int id;
        private String matNumber;
        private String templateId;

        public TemplateId() {}

        public TemplateId(int id, String matNumber, String templateId) {
            this.id = id;
            this.matNumber = matNumber;
            this.templateId = templateId;
        }

        public String insert(Context context) {
            ArrayList<HashMap<String, String>> hashMapArrayList = new ArrayList<>();
            hashMapArrayList.add(toHashMap());
            return insert(context, hashMapArrayList);
        }

        public int delete(Context context) {
            return delete(context, getColumns()[2] + "=?",
                    new String[]{getTemplateId()});
        }

        public int deleteWithMatNo(Context context) {
            return delete(context, getColumns()[1] + "=?",
                    new String[]{getMatNumber()});
        }

        public TemplateId get(Context context) {
            ArrayList<HashMap<String, String>> hashMapArrayList = get(context,
                    getColumns()[2]+"=?",
                    new String[]{getTemplateId()});
            if (hashMapArrayList.size() > 0) {
                return fromHashMap(hashMapArrayList.get(0));
            } else return null;
        }
        public TemplateId getWithMatNo(Context context) {
            ArrayList<HashMap<String, String>> hashMapArrayList = get(context,
                    getColumns()[1]+"=?",
                    new String[]{getMatNumber()});
            if (hashMapArrayList.size() > 0) {
                return fromHashMap(hashMapArrayList.get(0));
            } else return null;
        }

        public ArrayList<TemplateId> getAll(Context context) {
            ArrayList<TemplateId> templateIds = new ArrayList<>();
            ArrayList<HashMap<String, String>> hashMapArrayList = get(context, "", null);
            for (HashMap<String, String> stringHashMap : hashMapArrayList) {
                templateIds.add(fromHashMap(stringHashMap));
            }
            return templateIds;
        }

        public int updateWithTemplateId(Context context) {
            return update(context, toHashMap(), getColumns()[2]+"=?",
                    new String[]{getTemplateId()});
        }

        public int updateWithMatNo(Context context) {
            return update(context, toHashMap(), getColumns()[1]+"=?",
                    new String[]{getMatNumber()});
        }

        @Override
        public String getTableName() {
            return "templateIds";
        }

        @Override
        public String[] getColumns() {
            return new String[]{"id", "matNumber", "templateId"};
        }

        @Override
        protected String[] getColumnsType() {
            return new String[]{
                    "integer primary key autoincrement",
                    "text",
                    "text"
            };
        }

        private HashMap<String, String> toHashMap() {
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put(getColumns()[1], getMatNumber());
            stringHashMap.put(getColumns()[2], getTemplateId());

            return stringHashMap;
        }

        private TemplateId fromHashMap(HashMap<String, String> hashMap) {
            return new TemplateId(0,
                    hashMap.get(getColumns()[1]),
                    hashMap.get(getColumns()[2]));
        }

        public String getMatNumber() {
            return matNumber;
        }

        public String getTemplateId() {
            return templateId;
        }
    }
}