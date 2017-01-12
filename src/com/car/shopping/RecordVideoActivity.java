//获取分类：
//api_get_post_type
//返回数组，每个元素包含type_id,type_name.目前包含日系、欧美、国产
//
//发布状态：
//api_new_post
//传：post_type，分类，当前有3个，可传1,2,3
//text,文字内容
//pics,图片数组，每个元素为base64字符串，前缀为"data:image/png;base64," 注意把png替换为文件格式
//videos，视频数组，每个元素为base64字符串，前缀为"data:video/mp4;base64," 注意把mp4替换为文件格式
//需要在前台判断文件大小，过大的直接不让传。最好设得小一点，10M、20M什么的
//
//删除状态:
//api_delete_post
//传：post_id
//
//状态点赞:
//api_zan_post
//传：post_id，要点赞的状态的id
//
//取消点赞
//api_cancel_zan_post
//传：post_id，要取消点赞的状态的id
//
//收藏状态：
//api_save_post
//传：post_id，要收藏状态的id
//
//取消收藏状态：
//api_cancel_save_post
//传：post_id，要取消收藏状态的id
//
//评论状态：
//api_comment_post
//传
//post_id，状态id
//father_id，回复的评论的id，如直接评论状态，则为0
//father_user_type，回复的评论的发布者的类型,返回的数据里有user/type，如直接评论状态，则为0 
//father_user_id，回复的评论的发布者的id，如直接评论状态，则为0
//content，评论内容
//
//删除评论:
//api_delete_comment_post
//传comment_id，要删除的评论的id
//
//获取状态列表
//api_get_post_list
//传post_type_filter，保留哪些post_type，1,2,3用逗号隔开
//结果只返回最近的30条记录
//
//获取指定人的状态
//api_get_post_list_someone
//to_user_type,要访问的人的类型
//to_user_id，要访问的人的id
//
//获取收藏的状态
//api_get_post_list_saved
//
//获取参与评论的状态
//api_get_post_list_commented
//
//关于状态列表的说明：
//data.user_name、data.avatar、data.tel、data.user_type、data.user_id这些为获取指定人状态列表时，指定人的相关信息
//
//data.posts为状态的数组
//{
//    "id": "1",//状态id
//    "poster_type": "user",//发布者类型 user/shop
//    "poster_id": "1",//发布者id
//    "post_type": "1",//状态类型，日韩、国产
//    "post_text": "这是一条测试互动状态",//状态文字类容
//    "post_time": "2016-12-18 10:09:56",//发布时间
//    "attachments": [//附件数组
//    	{
//    	    "attachment_url": "/assets/post/rqsfegzfuyuihcqqwomsfrusphndsteetwrimzouczsbifrhjm.png",//附件链接
//          "attachment_type": "0"//附件类型，0表示图片，1表示视频
//      },
//      {
//          "attachment_url": "/assets/post/jbydhlhakbdndzrlnwqdzsgknefrphttdtoyroxddjmrzfmkgt.png",
//          "attachment_type": "0"
//      }
//    ],
//    "zan": 1,//是否赞过，0是没有，1是赞过
//    "save": 1,//是否已收藏
//    "comments": [//评论列表，已按时间排序，逐条显示即可
//        {
//            "id": "4",//评论id
//            "post_id": "1",
//            "father_id": "0",//回复的评论的id
//            "father_user_type": "0",//回复的评论的发布者的类型
//            "father_user_id": "0",//回复的评论的发布者的id
//            "user_type": "user",//发布该评论的人的类型
//            "user_id": "1",//发布该评论的人的id
//            "content": "这是评论1",//评论内容
//            "comment_time": "2016-12-18 11:24:46",//评论时间
//            "user_name": "天才",//发布该评论的人的姓名
//            "father_user_name": null//回复的评论的发布者的姓名
//        },
//        {
//            "id": "5",
//            "post_id": "1",
//            "father_id": "4",
//            "father_user_type": "user",
//            "father_user_id": "1",
//            "user_type": "user",
//            "user_id": "1",
//            "content": "回复评论1的评论2",
//            "comment_time": "2016-12-18 11:25:03",
//            "user_name": "天才",
//            "father_user_name": "天才"
//        },
//        {
//            "id": "6",
//            "post_id": "1",
//            "father_id": "4",
//            "father_user_type": "user",
//            "father_user_id": "1",
//            "user_type": "user",
//            "user_id": "1",
//            "content": "回复评论1的评论4",
//            "comment_time": "2016-12-18 11:25:07",
//            "user_name": "天才",
//            "father_user_name": "天才"
//        }
//    ],
//    "poster_name": "天才",//发布者姓名
//    "poster_im_username": "wpmklntcsezwdrlornunuyhpmkrdeccz",//发布者im账号
//    "poster_avatar": "/assets/Image/user_avatar/120×120px.png"//发布者头像
//}