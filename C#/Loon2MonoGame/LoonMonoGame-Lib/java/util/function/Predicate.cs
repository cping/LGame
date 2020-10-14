using java.lang;

namespace java.util.function 
{ 
    public interface Predicate
    {
        Predicate And(Predicate other);
        Predicate Negate();
        Predicate Or(Predicate other);
        bool Test(object s);
    }

    public static class Predicate_Java
    {
        public static Predicate And(Predicate @this, Predicate other)
        {   
            if (@this==null || other==null) { throw new NullPointerException(); }
            return new PredicateAnd(@this,other);
        }
        
        public static Predicate Negate(Predicate @this)
        {   
            if (@this==null) { throw new NullPointerException(); }
            return new PredicateNegate(@this);
        }
        
        public static Predicate Or(Predicate @this, Predicate other)
        {   
            if (@this==null || other==null) { throw new NullPointerException(); }
            return new PredicateOr(@this,other);
        }
        
        public static Predicate IsEqual(object tobj) 
        {   
            return new PredicateIsEqual(tobj);
        }
    }

    public class PredicateIsEqual : Predicate
    {
        private readonly object tobj;
        
        public PredicateIsEqual(object tobj) 
        {   
            this.tobj = tobj;
        }
        public virtual bool Test(object o)
        {   
            return tobj==null ? o==null : tobj.Equals(o);
        }
        public virtual Predicate And(Predicate other)
        {   
            return Predicate_Java.And(this,other);
        }
        public virtual Predicate Negate()
        {   
            return Predicate_Java.Negate(this);
        }
        public virtual Predicate Or(Predicate other)
        {   
            return Predicate_Java.Or(this,other);
        }
    }        

    public class PredicateAnd : PredicateIsEqual
    {
        private readonly Predicate a;
        private readonly Predicate b;
        
        public PredicateAnd(Predicate a, Predicate b) : base(null)
        {   
            this.a = a;
            this.b = b;
        }
        
        public override bool Test(object o)
        {   
            return a.Test(o) && b.Test(o);
        }
    }        

    public class PredicateNegate : PredicateIsEqual
    {
        private readonly Predicate a;
        
        public PredicateNegate(Predicate a) : base(null)
        {   
            this.a = a;
        }
        public override bool Test(object o)
        {   
            return !a.Test(o);
        }
    }
            

    public class PredicateOr : PredicateIsEqual
    {
        private readonly Predicate a;
        private readonly Predicate b;
        
        public PredicateOr(Predicate a, Predicate b) : base(null)
        {   
            this.a = a;
            this.b = b;
        }
        public override bool Test(object o)
        {   
            return a.Test(o) || b.Test(o);
        }
    }
}        
