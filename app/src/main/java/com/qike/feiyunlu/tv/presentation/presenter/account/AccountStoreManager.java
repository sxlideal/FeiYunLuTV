package com.qike.feiyunlu.tv.presentation.presenter.account;

import android.content.Context;

import com.qike.feiyunlu.tv.module.database.QikeDatabaseProvider;

import java.util.List;
import com.qike.feiyunlu.tv.presentation.model.dto.User;

/**
 * 
 *<p>用户信息的存储管理</p><br/>
 *<p>对用户进行增删改查,管理所有的用户信息</p>
 *		可能涉及到加密</p>
 * @since 1.0.0
 * @author xky
 */
public class AccountStoreManager {
	private static AccountStoreManager INSTANCE;
	private Context mContext;
	private QikeDatabaseProvider mDbImpl;
	
	
	private AccountStoreManager(Context context) {
		//TODO 对数据库进行初始化
		mContext = context;
		initStore();
	}

	private void initStore() {
		mDbImpl = new QikeDatabaseProvider(mContext,StorePath.getUsersDbStorePath(mContext),QikeDatabaseProvider.VIDEO_USER_DB); 
	}

	public synchronized static AccountStoreManager getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new AccountStoreManager(context);
		}
		return INSTANCE;
	}
	/**
	 * 
	 *<p>保存最新用户</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 */
	public void saveUser(User user) {
		user.setIslast(1);
		User lastUser = AccountManager.getInstance(mContext).getUser();
		if(lastUser != null){
			//上次最新
			lastUser.setIslast(0);
			mDbImpl.update(lastUser);
		}
		AccountManager.getInstance(mContext).setUser(user);
		mDbImpl.save(user);
	}
	
	/**
	 * 
	 *<p>删除用户记录信息</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 */
	public void deleteUser(User user) {
		mDbImpl.delete(user);
	}

	/**
	 * 
	 *<p>更新用户记录</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 */
	public void updateUser(User user) {
		mDbImpl.update(user);
	}
	/**
	 * 
	 *<p>查询某个用户记录,根据用户id</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 * @return
	 */
	public User queryUser(String userid) {
		return mDbImpl.query(User.class, userid);
	}
	/**
	 * 
	 *<p>查询最新用户</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 * @return
	 */
	public User queryNewUser() {
		List<User> list = mDbImpl.queryForEq(User.class, "islast", 1);
		return list == null ? null:list.size()>0?list.get(0):null;
	}
	/**
	 * 
	 *<p>模糊查询用户</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 * @return
	 */
	public User queryUserById(String userid) {
//		List<User> list = mDbImpl.queryForEq(User.class,"userid", userid+"%");
//		return list == null ? null:list.size()>0?list.get(0):null;
		//模糊查询规则，该开源数据库的模糊匹配不知道有没有
		//没有只有手动匹配了 先查询出所有的的 然后在匹配
		return null;
	}
	/**
	 * 
	 *<p>查询所有的用户记录</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xky
	 * @return
	 */
	public List<User> queryUsers() {
		return mDbImpl.queryAll(User.class);
	}

}
