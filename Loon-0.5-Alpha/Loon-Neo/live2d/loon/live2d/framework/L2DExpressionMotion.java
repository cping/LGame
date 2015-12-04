package loon.live2d.framework;

import loon.live2d.ALive2DModel;
import loon.live2d.motion.AMotion;
import loon.live2d.motion.MotionQueueManager.MotionQueueEnt;
import loon.live2d.util.Json;
import loon.live2d.util.Json.Value;
import loon.utils.ArrayByte;
import loon.utils.ListMap;
import loon.utils.TArray;

public class L2DExpressionMotion extends AMotion{
	
	static private final String EXPRESSION_DEFAULT = "DEFAULT";
	static public final int TYPE_SET=0;
	static public final int TYPE_ADD=1;
	static public final int TYPE_MULT=2;

	private TArray<L2DExpressionParam> paramList;


	public L2DExpressionMotion()
	{
		paramList=new TArray<L2DExpressionParam>();
	}

	@Override
	public void updateParamExe(ALive2DModel model, long timeMSec, float weight, MotionQueueEnt motionQueueEnt)
	{
		for ( int i = paramList.size -1 ; i >= 0 ; --i )
		{
			L2DExpressionParam param = paramList.get(i) ;
			if(param.type == TYPE_ADD)
			{
				model.addToParamFloat( param.id, param.value , weight ) ;// 相対変化 加算
			}
			else if(param.type == TYPE_MULT)
			{
				model.multParamFloat( param.id, param.value , weight ) ;// 相対変化 乗算
			}
			else if(param.type == TYPE_SET)
			{
				model.setParamFloat( param.id	, param.value , weight ) ;// 絶対変化
			}
		}
	}


	public static L2DExpressionMotion loadJson(ArrayByte in) throws Exception
	{
		byte[] buf = in.getData() ;
		return loadJson(buf);
	}

	public static L2DExpressionMotion loadJson(byte[] buf ) throws Exception
	{
		L2DExpressionMotion ret = new L2DExpressionMotion();

		Value json = Json.parseFromBytes( buf ) ;


		ret.setFadeIn(json.get("fade_in").toInt(1000));
		ret.setFadeOut(json.get("fade_out").toInt(1000));

		if(json.get("params")==null)return ret;

		Value params = json.get("params");
		int paramNum = params.getVector(null).size;

		ret.paramList = new TArray<L2DExpressionParam>( paramNum );

		for (int i = 0; i < paramNum; i++)
		{
			Value param = params.get(i);
			String paramID=param.get("id").toString();
			float value = param.get("val").toFloat();

			int calcTypeInt=TYPE_ADD;
			String calc = param.get("calc")!=null? param.get("calc").toString() : "add";
			if( calc.equals("add") )
			{
				calcTypeInt = TYPE_ADD;
			}
			else if( calc.equals("mult") )
			{
				calcTypeInt = TYPE_MULT;
			}
			else if( calc.equals("set") )
			{
				calcTypeInt = TYPE_SET;
			}
			else
			{
				calcTypeInt = TYPE_ADD;
			}

			if( calcTypeInt == TYPE_ADD)
			{
				float defaultValue = param.get("def") == null ? 0 : param.get("def").toFloat();
				value = value - defaultValue;
			}
			else if( calcTypeInt == TYPE_MULT)
			{
				float defaultValue = param.get("def") == null ? 1 : param.get("def").toFloat(0);
				if( defaultValue == 0 )defaultValue = 1;// 0(不正値)を指定した場合は1(標準)にする
				value = value / defaultValue;
			}

			L2DExpressionParam item=new L2DExpressionParam();

			item.id=paramID;
			item.type=calcTypeInt;
			item.value=value;

			ret.paramList.add(item);
		}
		return ret;
	}


	static public ListMap<String,AMotion> loadExpressionJsonV09(ArrayByte in ) throws Exception
	{
		ListMap<String, AMotion> expressions = new ListMap<String, AMotion>() ;

		byte[] buf =in.getData();

		Value mo = Json.parseFromBytes( buf ) ;

		Value defaultExpr = mo.get(EXPRESSION_DEFAULT) ;

		Object[] keys = mo.keySet() ;
		for(Object o : keys)
		{
			String key = (String)o;
			if( EXPRESSION_DEFAULT.equals( key ) ) continue ;

			Value expr = mo.get( key ) ;

			L2DExpressionMotion exMotion = loadJsonV09( defaultExpr , expr) ;
			expressions.put( key , exMotion ) ;
		}

		return expressions ;
	}

	static private L2DExpressionMotion loadJsonV09( Value defaultExpr , Value expr ){

		L2DExpressionMotion ret=new L2DExpressionMotion();
		ret.setFadeIn( expr.get("FADE_IN").toInt(1000) ) ;
		ret.setFadeOut( expr.get("FADE_OUT").toInt(1000) ) ;

		Value defaultParams = defaultExpr.get("PARAMS") ;
		Value params = expr.get("PARAMS") ;

		Object[] paramID = params.keySet() ;
		TArray<String> idList = new TArray<String>() ;

		for(Object o : paramID)
		{
			idList.add( (String)o ) ;
		}

		for ( int i = idList.size -1 ; i >= 0 ; --i )
		{
			String id = idList.get(i) ;

			float defaultV = defaultParams.get(id).toFloat(0) ;
			float v = params.get( id ).toFloat( 0.0f ) ;
			float value = ( v - defaultV ) ;
//			ret.addParam(id, value,L2DExpressionMotion.TYPE_ADD);
			L2DExpressionParam param=new L2DExpressionParam();
			param.id=id;
			param.type=L2DExpressionMotion.TYPE_ADD;
			param.value=value;
			ret.paramList.add(param);
		}

		return ret;
	}


	static public class L2DExpressionParam {
		public String id;
		//public int index=-1;
		public int type;
		public float value;
	}

}
