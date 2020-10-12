package services

import config.MyAwait
import dao.AliyunKeyDao
import javax.inject.{Inject, Singleton}
import utils.SendValidMessage

@Singleton
class OnStart @Inject()(aliyunKeyDao:AliyunKeyDao) extends MyAwait{


  SendValidMessage.aliyunKeyRow = aliyunKeyDao.getKey.toAwait

  println("[info] 阿里云 Key 加载成功！")

  SendValidMessage.validConfig

  println("[info] 手机验证码池初始化成功！")


}
