package sample.db;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.template.server.database.util.mongo.MongoUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

/**
 * @author huydn on 09/04/2024 21:53
 */
public class TestMongoUtil {

    public static void main(String[] args) {
        Config.loadConfig();

        MongoDatabase mongoDB = MongoUtil.getDatabase("mongodb://root:123456@localhost:27017/", "cogover");
        MongoCollection<AuthToken> tokenCollection = mongoDB.getCollection("auth_token", AuthToken.class);

//        AuthToken grade = AuthToken.builder()
//                .tokenId("id-123")
//                .tokenSecretKey("key")
//                .build();
//        grades.insertOne(grade);
//        System.out.println("AuthToken inserted.");

        // find this grade.
        Bson filter = Filters.eq("device_id", "device-abc-123");
        AuthToken token2 = tokenCollection.find(filter).first();
        System.out.println("++++++++++ found:\t" + token2.getId().toString());

//        Bson filter3 = Filters.eq("_id", new ObjectId("661506cdedab8324335e154d"));
//        AuthToken token3 = tokenCollection.find(filter3).first();
//        System.out.println("++++++++++ filter3:\t" + token3.getId().toHexString());

        // update this grade: adding an exam grade
//        List<Score> newScores = new ArrayList<>(grade.getScores());
//        newScores.add(new Score().setType("exam").setScore(42d));
//        grade.setScores(newScores);
//        Bson filterByGradeId = eq("_id", grade.getId());
//        FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
//        Grade updatedGrade = grades.findOneAndReplace(filterByGradeId, grade, returnDocAfterReplace);
//        System.out.println("Grade replaced:\t" + updatedGrade);
//
//        // delete this grade
//        System.out.println("Grade deleted:\t" + grades.deleteOne(filterByGradeId));
    }

}
