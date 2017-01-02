@Grapes(
        @Grab(group='com.amazonaws', module='aws-java-sdk', version='1.11.75')
)

import com.amazonaws.services.s3.AmazonS3Client

def cli = new CliBuilder(usage:'s3grep -[b] [searchString]',
        header:'Options:')


cli.with {
        h longOpt: 'help', 'Show usage information'
        b longOpt: 'bucketName', args: 1, argName: 'bucketName', 'S3 bucket name'
}

def options = cli.parse(args)
if (!options) {
        return
}
// Show usage text when -h or --help option is used.
if (options.h) {
        cli.usage()
        return
}

def bucket = options.b
def searchString = options.arguments()

AmazonS3Client s3 = new AmazonS3Client();
def objectList = s3.listObjects(bucket)
objectList.objectSummaries.collect {
         it.key
}.findAll{
        def inputStream = s3.getObject(bucket,it).getObjectContent()
        def result = inputStream.readLines().find{ line -> searchString.find { s -> line.contains(s) } }
        result != null
}.each { println it}