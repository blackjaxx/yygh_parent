package com.atguigu.yygh.vo.order;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 预约记录Excel导出VO
 */
@Data
public class OrderExcelVo {

    @ExcelProperty("医院名称")
    @ColumnWidth(25)
    private String hosname;

    @ExcelProperty("科室")
    @ColumnWidth(15)
    private String depname;

    @ExcelProperty("医生职称")
    @ColumnWidth(15)
    private String title;

    @ExcelProperty("就诊人")
    @ColumnWidth(10)
    private String patientName;

    @ExcelProperty("就诊日期")
    @ColumnWidth(15)
    private String reserveDate;

    @ExcelProperty("就诊时间")
    @ColumnWidth(10)
    private String reserveTime;

    @ExcelProperty("挂号费(元)")
    @ColumnWidth(12)
    private String amount;

    @ExcelProperty("订单状态")
    @ColumnWidth(12)
    private String orderStatus;
}