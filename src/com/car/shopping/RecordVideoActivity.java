//��ȡ���ࣺ
//api_get_post_type
//�������飬ÿ��Ԫ�ذ���type_id,type_name.Ŀǰ������ϵ��ŷ��������
//
//����״̬��
//api_new_post
//����post_type�����࣬��ǰ��3�����ɴ�1,2,3
//text,��������
//pics,ͼƬ���飬ÿ��Ԫ��Ϊbase64�ַ�����ǰ׺Ϊ"data:image/png;base64," ע���png�滻Ϊ�ļ���ʽ
//videos����Ƶ���飬ÿ��Ԫ��Ϊbase64�ַ�����ǰ׺Ϊ"data:video/mp4;base64," ע���mp4�滻Ϊ�ļ���ʽ
//��Ҫ��ǰ̨�ж��ļ���С�������ֱ�Ӳ��ô���������Сһ�㣬10M��20Mʲô��
//
//ɾ��״̬:
//api_delete_post
//����post_id
//
//״̬����:
//api_zan_post
//����post_id��Ҫ���޵�״̬��id
//
//ȡ������
//api_cancel_zan_post
//����post_id��Ҫȡ�����޵�״̬��id
//
//�ղ�״̬��
//api_save_post
//����post_id��Ҫ�ղ�״̬��id
//
//ȡ���ղ�״̬��
//api_cancel_save_post
//����post_id��Ҫȡ���ղ�״̬��id
//
//����״̬��
//api_comment_post
//��
//post_id��״̬id
//father_id���ظ������۵�id����ֱ������״̬����Ϊ0
//father_user_type���ظ������۵ķ����ߵ�����,���ص���������user/type����ֱ������״̬����Ϊ0 
//father_user_id���ظ������۵ķ����ߵ�id����ֱ������״̬����Ϊ0
//content����������
//
//ɾ������:
//api_delete_comment_post
//��comment_id��Ҫɾ�������۵�id
//
//��ȡ״̬�б�
//api_get_post_list
//��post_type_filter��������Щpost_type��1,2,3�ö��Ÿ���
//���ֻ���������30����¼
//
//��ȡָ���˵�״̬
//api_get_post_list_someone
//to_user_type,Ҫ���ʵ��˵�����
//to_user_id��Ҫ���ʵ��˵�id
//
//��ȡ�ղص�״̬
//api_get_post_list_saved
//
//��ȡ�������۵�״̬
//api_get_post_list_commented
//
//����״̬�б��˵����
//data.user_name��data.avatar��data.tel��data.user_type��data.user_id��ЩΪ��ȡָ����״̬�б�ʱ��ָ���˵������Ϣ
//
//data.postsΪ״̬������
//{
//    "id": "1",//״̬id
//    "poster_type": "user",//���������� user/shop
//    "poster_id": "1",//������id
//    "post_type": "1",//״̬���ͣ��պ�������
//    "post_text": "����һ�����Ի���״̬",//״̬��������
//    "post_time": "2016-12-18 10:09:56",//����ʱ��
//    "attachments": [//��������
//    	{
//    	    "attachment_url": "/assets/post/rqsfegzfuyuihcqqwomsfrusphndsteetwrimzouczsbifrhjm.png",//��������
//          "attachment_type": "0"//�������ͣ�0��ʾͼƬ��1��ʾ��Ƶ
//      },
//      {
//          "attachment_url": "/assets/post/jbydhlhakbdndzrlnwqdzsgknefrphttdtoyroxddjmrzfmkgt.png",
//          "attachment_type": "0"
//      }
//    ],
//    "zan": 1,//�Ƿ��޹���0��û�У�1���޹�
//    "save": 1,//�Ƿ����ղ�
//    "comments": [//�����б��Ѱ�ʱ������������ʾ����
//        {
//            "id": "4",//����id
//            "post_id": "1",
//            "father_id": "0",//�ظ������۵�id
//            "father_user_type": "0",//�ظ������۵ķ����ߵ�����
//            "father_user_id": "0",//�ظ������۵ķ����ߵ�id
//            "user_type": "user",//���������۵��˵�����
//            "user_id": "1",//���������۵��˵�id
//            "content": "��������1",//��������
//            "comment_time": "2016-12-18 11:24:46",//����ʱ��
//            "user_name": "���",//���������۵��˵�����
//            "father_user_name": null//�ظ������۵ķ����ߵ�����
//        },
//        {
//            "id": "5",
//            "post_id": "1",
//            "father_id": "4",
//            "father_user_type": "user",
//            "father_user_id": "1",
//            "user_type": "user",
//            "user_id": "1",
//            "content": "�ظ�����1������2",
//            "comment_time": "2016-12-18 11:25:03",
//            "user_name": "���",
//            "father_user_name": "���"
//        },
//        {
//            "id": "6",
//            "post_id": "1",
//            "father_id": "4",
//            "father_user_type": "user",
//            "father_user_id": "1",
//            "user_type": "user",
//            "user_id": "1",
//            "content": "�ظ�����1������4",
//            "comment_time": "2016-12-18 11:25:07",
//            "user_name": "���",
//            "father_user_name": "���"
//        }
//    ],
//    "poster_name": "���",//����������
//    "poster_im_username": "wpmklntcsezwdrlornunuyhpmkrdeccz",//������im�˺�
//    "poster_avatar": "/assets/Image/user_avatar/120��120px.png"//������ͷ��
//}