package com.roamtech.telephony.roamapp.util;

import com.roamtech.telephony.roamapp.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenblue23 on 2016/9/13.
 */
public class AreaMap {
    private static Map<String, Integer> map;

    static {
        map = new HashMap<>();
        map.put("爱尔兰", R.drawable.ic_area_irl);
        map.put("奥地利", R.drawable.ic_area_aut);
        map.put("澳大利亚", R.drawable.ic_area_aus);
        map.put("澳门", R.drawable.ic_area_mac);
        map.put("比利时", R.drawable.ic_area_bel);
        map.put("冰岛", R.drawable.ic_area_isl);
        map.put("波兰", R.drawable.ic_area_pol);
        map.put("大洋洲", R.drawable.ic_area_oceania);
        map.put("丹麦", R.drawable.ic_area_dnk);
        map.put("德国", R.drawable.ic_area_deu);
        map.put("俄罗斯", R.drawable.ic_area_rus);
        map.put("法国", R.drawable.ic_area_fra);
        map.put("芬兰", R.drawable.ic_area_fin);
        map.put("荷兰", R.drawable.ic_area_nld);
        map.put("加拿大", R.drawable.ic_area_can);
        map.put("捷克", R.drawable.ic_area_cze);
        map.put("罗马尼亚", R.drawable.ic_area_rou);
        map.put("马耳他", R.drawable.ic_area_mlt);
        map.put("马来西亚", R.drawable.ic_area_mys);
        map.put("美国", R.drawable.ic_area_usa);
        map.put("美洲", R.drawable.ic_area_america);
        map.put("挪威", R.drawable.ic_area_nor);
        map.put("欧洲", R.drawable.ic_area_europe);
        map.put("葡萄牙", R.drawable.ic_area_prt);
        map.put("全球", R.drawable.ic_area_globle);
        map.put("瑞典", R.drawable.ic_area_swe);
        map.put("瑞士", R.drawable.ic_area_che);
        map.put("斯洛伐克", R.drawable.ic_area_svk);
        map.put("台湾", R.drawable.ic_area_twn);
        map.put("泰国", R.drawable.ic_area_tha);
        map.put("土耳其", R.drawable.ic_area_tur);
        map.put("西班牙", R.drawable.ic_area_esp);
        map.put("希腊", R.drawable.ic_area_grc);
        map.put("香港", R.drawable.ic_area_hkg);
        map.put("新加坡", R.drawable.ic_area_sgp);
        map.put("新西兰", R.drawable.ic_area_nzl);
        map.put("匈牙利", R.drawable.ic_area_hun);
        map.put("亚洲", R.drawable.ic_area_asia);
        map.put("意大利", R.drawable.ic_area_ita);
        map.put("英国", R.drawable.ic_area_gbr);
    }

    public static Integer getResId(String areaName) {
        return map.get(areaName);
    }
}
