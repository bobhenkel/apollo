# apollo
Apollo - The logz.io continuous deployment solution over kubernetes

# Technical Debt
 - Move to Guice, (especially for MyBatis). Currently each DAO need to be requested on the method scope, since MyBatis SqlSession is not thread safe

