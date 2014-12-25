package jsmath;

/**
 * 包装 jsMath.TeXparams 的对象类型.
 *
 * (问题) 如果 CTeX 字段发生了变化, 则理论上这里的字段也要检查看是否要变化.
 */
public class TeXParam {
	/**
	 * 构造一个 CTeXParam 的新实例, 使用指定的比例因子 factor, 从 JsMath.TeX 中得到相应的数据并乘上该缩放比例.
	 * @param scale
	 */
	public TeXParam(int factor) {
		this.thinmuskip = CTeX.thinmuskip * factor/100;
		this.medmuskip = CTeX.medmuskip * factor/100;
		this.thickmuskip = CTeX.axis_height * factor/100;
		
		this.x_height = CTeX.x_height * factor/100;
		this.quad = CTeX.quad * factor/100;
		this.num1 = CTeX.num1 * factor/100;
		this.num2 = CTeX.num2 * factor/100;
		this.num3 = CTeX.num3 * factor/100;
		this.denom1 = CTeX.denom1 * factor/100;
		this.denom2 = CTeX.denom2 * factor/100;
		this.sup1 = CTeX.sup1 * factor/100;
		this.sup2 = CTeX.sup2 * factor/100;
		this.sup3 = CTeX.sup3 * factor/100;
		this.sub1 = CTeX.sub1 * factor/100;
		this.sub2 = CTeX.sub2 * factor/100;
		this.sup_drop = CTeX.sup_drop * factor/100;
		this.sub_drop = CTeX.sub_drop * factor/100;
		this.delim1 = CTeX.delim1 * factor/100;
		this.delim2 = CTeX.delim2 * factor/100;
		this.axis_height = CTeX.axis_height * factor/100;
		this.default_rule_thickness = CTeX.default_rule_thickness * factor/100;
		this.big_op_spacing1 = CTeX.big_op_spacing1 * factor/100;
		this.big_op_spacing2 = CTeX.big_op_spacing2 * factor/100;
		this.big_op_spacing3 = CTeX.big_op_spacing3 * factor/100;
		this.big_op_spacing4 = CTeX.big_op_spacing4 * factor/100;
		this.big_op_spacing5 = CTeX.big_op_spacing5 * factor/100;
		
		// conversion of em's to TeX internal integer
		this.integer = CTeX.integer * factor/100;
		this.scriptspace = CTeX.scriptspace * factor/100;
		this.nulldelimiterspace = CTeX.nulldelimiterspace * factor/100;
		this.delimiterfactor = (int)(CTeX.delimiterfactor * factor/100);
		this.delimitershortfall = CTeX.delimitershortfall * factor/100;
		// scaling factor for font dimensions
		this.scale = CTeX.scale * factor/100f;
		
		this.M_height = CTeX.M_height * factor/100f;
		this.h = CTeX.h * factor/100;
		this.d = CTeX.d * factor/100;
		this.hd = CTeX.hd * factor/100;
	}
	
	// The TeX font parameters; 这些参数应该描述在 TeX Book 附录中(epsilon~X, tao~X)
	public float thinmuskip = 3.0f/18f;
	public float medmuskip = 4.0f/18f;
	public float thickmuskip = 5.0f/18f;
	
	public float x_height = 0.430554f;
	public float quad = 1.0f;
	public float num1 = 0.676508f;
	public float num2 = 0.393732f;
	public float num3 = 0.44373f;
	public float denom1 = 0.685951f;
	public float denom2 = 0.344841f;
	public float sup1 = 0.412892f;
	public float sup2 = 0.362893f;
	public float sup3 = 0.288888f;
	public float sub1 = 0.15f;
	public float sub2 = 0.247217f;
	public float sup_drop = 0.386108f;
	public float sub_drop = 0.05f;
	public float delim1 = 2.39f;
	public float delim2 = 1.0f;
	public float axis_height = 0.25f;
	public float default_rule_thickness = 0.06f;
	public float big_op_spacing1 = 0.111111f;
	public float big_op_spacing2 = 0.166666f;
	public float big_op_spacing3 =  0.2f;
	public float big_op_spacing4 =  0.6f;
	public float big_op_spacing5 =  0.1f;

	// conversion of em's to TeX internal integer
	public float integer =          6553.6f;
	public float scriptspace =        0.05f;
	public float nulldelimiterspace =  0.12f;
	public int delimiterfactor =     901;
	public float delimitershortfall =   0.5f;
	// scaling factor for font dimensions
	public float scale =                 1;

	// TODO: 这里需要更精确的数值.
	public float M_height = 0.823f;
	public float h = 0.8866f;
	public float d = 0.1209f;
	public float hd = 1.0075f;

}
