package com.darryl.license.sign;

import com.darryl.license.model.LicenseChecker;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Auther: Darryl
 * @Description: 获取服务硬件信息，抽象模版
 * @Date: 2022/08/31
 */
@Slf4j
public abstract class AbstractObtainServerInfos {

	/**
	 * 组装需要额外校验的License参数
	 */
	public LicenseChecker getServerInfos() {
		LicenseChecker result = new LicenseChecker();

		try {
			result.setIpAddress(this.getIpAddress());
			result.setMacAddress(this.getMacAddress());
			result.setCpuSerial(this.getCPUSerial());
			result.setMainBoardSerial(this.getMainBoardSerial());
		} catch (Exception e) {
			log.error("获取服务器硬件信息失败", e);
		}

		return result;
	}

	/**
	 * 获取当前服务器所有符合条件的InetAddress
	 */
	protected List<InetAddress> getLocalAllInetAddress() throws Exception {
		List<InetAddress> result = new ArrayList<>(4);

		// 遍历所有的网络接口
		for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
			NetworkInterface iface = (NetworkInterface) networkInterfaces.nextElement();
			// 在所有的接口下再遍历IP
			for (Enumeration inetAddresses = iface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
				InetAddress inetAddr = (InetAddress) inetAddresses.nextElement();

				//排除LoopbackAddress、LinkLocalAddress、MulticastAddress类型的IP地址
				if (!inetAddr.isLoopbackAddress() && !inetAddr.isLinkLocalAddress() && !inetAddr.isMulticastAddress()) {
					result.add(inetAddr);
				}
			}
		}
		return result;
	}

	/**
	 * 获取某个网络接口的Mac地址
	 */
	protected String getMacByInetAddress(InetAddress inetAddr) {
		try {
			byte[] mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
			StringBuffer stringBuffer = new StringBuffer();

			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					stringBuffer.append("-");
				}

				//将十六进制byte转化为字符串
				String temp = Integer.toHexString(mac[i] & 0xff);
				if (temp.length() == 1) {
					stringBuffer.append("0" + temp);
				} else {
					stringBuffer.append(temp);
				}
			}

			return stringBuffer.toString().toUpperCase();
		} catch (SocketException e) {
			log.error("get mac by inet address error, ", e);
		}
		return null;
	}


	/**
	 * 获取主板序列号
	 *
	 * @return
	 */
	protected abstract String getMainBoardSerial();

	/**
	 * 获取 CPU 序列号
	 *
	 * @return
	 */
	protected abstract String getCPUSerial();

	/**
	 * 获取 mac 地址
	 *
	 * @return
	 */
	protected abstract List<String> getMacAddress();

	/**
	 * 获取 IP 地址
	 *
	 * @return
	 */
	protected abstract List<String> getIpAddress();

}
