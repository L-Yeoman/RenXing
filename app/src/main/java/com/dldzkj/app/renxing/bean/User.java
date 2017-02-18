package com.dldzkj.app.renxing.bean;

import java.io.Serializable;
import java.util.Date;

import com.dldzkj.app.renxing.utils.Location;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * 用户实例类
 *
 * @author Administrator
 */
@Table(name = "users")// 建议加上注解， 混淆后表名不受影响
public class User implements Serializable {
    @Id
    @NoAutoIncrement
    private int User_ID;//用户标识
    @Column(column = "name")
    private String UserName;//用户账号
    @Transient  // Transient使这个列被忽略，不存入数据库
    public String BackGround;
    //    public static String staticFieldWillIgnore; // 静态字段也不会存入数据库
    public String Signature;//签名
    private String PassWord;//登录密码
    private String NicName;//昵称
    private String Email;//邮箱

    public String getProvince_ID() {
        return Province_ID;
    }

    public void setProvince_ID(String province_ID) {
        Province_ID = province_ID;
    }

    private String RealName;//真实姓名
    private String Sex;// 性别
    private String Area;//所属地区逗号分隔
    private String Birthday;//生日
    private String Address;//详情地址
    private String Mobile;//手机
    private String Portrait;// 头像
    private int Point;// 积分
    private float Amount;//余额
    private int GroupID;//等级
    private long Exp;//经验值
    private int Status;// 状态（0：正常，1：待验证，2：待审核，3：锁定）
    private int Is_Silent;//是否禁言（0：不禁言，1：禁言）
    private int Is_Access;//是否可以访问（0：禁止，1：可以）
    private String CloseDesc;//封号原因
    private String RegisterIP;//注册地址
    private Date RegisterDate;//注册时间
    private String Province_ID;
    private String City_ID;

    public String getCity_ID() {
        return City_ID;
    }

    public void setCity_ID(String city_ID) {
        City_ID = city_ID;
    }

    public String getArea_ID() {
        return Area_ID;
    }

    public void setArea_ID(String area_ID) {
        Area_ID = area_ID;
    }

    private String Area_ID;


    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getNicName() {
        return NicName;
    }

    public void setNicName(String nicName) {
        NicName = nicName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getSex() {

        return Sex;
    }
    public String getMySex(){
          String s="";
        if(Sex==null){
            return  s;
        }
        if(Sex.equals("0")){
            s="女";
        }else if(Sex.equals("1")){
            s="男";
        }
        return s;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getArea() {
       /* String s="";
        String Province=Location.getEachAddress(getProvince_ID()+"");
        String City= Location.getEachAddress(getCity_ID() + "");
        String Area=Location.getEachAddress(getArea_ID()+"");
        s=Province+" "+City+" "+Area;*/
        return Area;
    }
    public String getMyArea(){
        String s="";
        String Province=Location.getEachAddress(getProvince_ID()+"");
        String City= Location.getEachAddress(getCity_ID() + "");
        String Area=Location.getEachAddress(getArea_ID()+"");
        s=Province+" "+City+" "+Area;
        return s;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPortrait() {
        return Portrait;
    }

    public void setPortrait(String portrait) {
        Portrait = portrait;
    }

    public int getPoint() {
        return Point;
    }

    public void setPoint(int point) {
        Point = point;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public long getExp() {
        return Exp;
    }

    public void setExp(long exp) {
        Exp = exp;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getIs_Silent() {
        return Is_Silent;
    }

    public void setIs_Silent(int is_Silent) {
        Is_Silent = is_Silent;
    }

    public int getIs_Access() {
        return Is_Access;
    }

    public void setIs_Access(int is_Access) {
        Is_Access = is_Access;
    }

    public String getCloseDesc() {
        return CloseDesc;
    }

    public void setCloseDesc(String closeDesc) {
        CloseDesc = closeDesc;
    }

    public String getRegisterIP() {
        return RegisterIP;
    }

    public void setRegisterIP(String registerIP) {
        RegisterIP = registerIP;
    }

    public Date getRegisterDate() {
        return RegisterDate;
    }

    public void setRegisterDate(Date registerDate) {
        RegisterDate = registerDate;
    }

    public String getBackGround() {
        return BackGround;
    }

    public void setBackGround(String backGround) {
        BackGround = backGround;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }
}
