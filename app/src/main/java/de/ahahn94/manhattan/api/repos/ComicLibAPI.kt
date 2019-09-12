package de.ahahn94.manhattan.api.repos

import de.ahahn94.manhattan.api.responses.*
import de.ahahn94.manhattan.model.entities.IssueEntity
import de.ahahn94.manhattan.model.views.VolumeReadStatusView
import de.ahahn94.manhattan.api.clients.TrustedCertificatesClientFactory
import de.ahahn94.manhattan.utils.security.Authentication
import de.ahahn94.manhattan.utils.settings.Credentials
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Class that handles the json part of the ComicLib API.
 */
class ComicLibAPI(url: String) {

    companion object {

        // Constants.
        private const val API_V1_BASE_PATH = "/api/v1/"

    }

    // Instance of the API.
    private var instance: ComicLibApiInterface

    // Authentication strings for the authorization headers.
    private var basicAuthentication: String
    private var bearerTokenAuthentication: String

    // Init-block of the default constructor. Init instance and authentication strings.
    init {
        instance = Retrofit.Builder()
            .baseUrl(url + API_V1_BASE_PATH)
            .addConverterFactory(GsonConverterFactory.create())

            .client(TrustedCertificatesClientFactory.create())

            .build().create(ComicLibApiInterface::class.java)
        val credentials = Credentials.getInstance()
        basicAuthentication =
            Authentication.generateBasicAuthHeader(credentials.username, credentials.password)
        bearerTokenAuthentication = Authentication.generateBearerTokenHeader(credentials.apiKey)
    }

    /**
     * Get the bearer token of the logged-in user from the /tokens resource.
     * Returns a Response with a Token that may be null.
     */
    fun getToken(): Response<Token?> {
        return instance.getToken(basicAuthentication).execute()
    }

    /**
     * Get the issues list from the /issues resource.
     * Returns a Response with an IssueResponse.List that may be null.
     */
    fun getIssues(): Response<IssueResponse.List?> {
        return instance.getIssues(bearerTokenAuthentication).execute()
    }

    /**
     * Get the issue with the specified issueID from the /issues/{id} resource.
     * Returns a Response with an IssueResponse that may be null.
     */
    fun getIssue(issueID: String): Response<IssueResponse?> {
        return instance.getIssue(bearerTokenAuthentication, issueID).execute()
    }

    /**
     * Get the read-status of the issue with the specified issueID from the
     * /issues/{id}/readstatus resource.
     * Returns a Response with an IssueReadStatus that may be null.
     */
    fun getIssueReadStatus(issueID: String): Response<IssueReadStatus?> {
        return instance.getIssueReadStatus(bearerTokenAuthentication, issueID).execute()
    }

    /**
     * Update the read-status of the issue with the specified issueID for the logged-in user on the
     * /issues/{id}/readstatus resource.
     * Returns a Response with an IssueReadStatus of the updated dataset that may be null.
     */
    fun putIssueReadStatus(
        issueID: String,
        issueReadStatus: IssueEntity.ReadStatus
    ): Response<IssueReadStatus?> {
        return instance.putIssueReadStatus(bearerTokenAuthentication, issueID, issueReadStatus)
            .execute()
    }

    /**
     * Update the read-status of the issue with the specified issueID for the logged-in user on the
     * /issues/{id}/readstatus resource.
     * Returns a Response with an IssueReadStatus of the updated dataset that may be null.
     */
    fun putIssueReadStatus(
        issueID: String,
        issueReadStatus: IssueReadStatus.Content
    ): Response<IssueReadStatus?> {
        return instance.putIssueReadStatus(bearerTokenAuthentication, issueID, issueReadStatus)
            .execute()
    }

    /**
     * Get the volumes list from the /volumes resource.
     * Returns a Response with an VolumeResponse.List that may be null.
     */
    fun getVolumes(): Response<VolumeResponse.List?> {
        return instance.getVolumes(bearerTokenAuthentication).execute()
    }

    /**
     * Get the volume with the specified volumeID from the /volumes/{id} resource.
     * Returns a Response with a VolumeResponse that may be null.
     */
    fun getVolume(volumeID: String): Response<VolumeResponse?> {
        return instance.getVolume(bearerTokenAuthentication, volumeID).execute()
    }

    /**
     * Get the issues list of the volume specified by volumeID from the /volumes/{id}/issues resource.
     * Returns a Response with an IssueResponse.List that may be null.
     */
    fun getVolumeIssues(volumeID: String): Response<IssueResponse.List?> {
        return instance.getVolumeIssues(bearerTokenAuthentication, volumeID).execute()
    }

    /**
     * Get the read-status of the volume with the specified volumeID from the
     * /volumes/{id}/readstatus resource.
     * Returns a Response with a VolumeReadStatus that may be null.
     */
    fun getVolumeReadStatus(volumeID: String): Response<VolumeReadStatus?> {
        return instance.getVolumeReadStatus(bearerTokenAuthentication, volumeID).execute()
    }

    /**
     * Update the read-status of the volume with the specified volumeID for the logged-in user on the
     * /volumes/{id}/readstatus resource.
     * Returns a Response with a VolumeReadStatus of the updated dataset that may be null.
     */
    fun putVolumeReadStatus(
        volumeID: String,
        volumeReadStatus: VolumeReadStatusView
    ): Response<VolumeReadStatus?> {
        return instance.putVolumeReadStatus(bearerTokenAuthentication, volumeID, volumeReadStatus)
            .execute()
    }

    /**
     * Update the read-status of the volume with the specified volumeID for the logged-in user on the
     * /volumes/{id}/readstatus resource.
     * Returns a Response with a VolumeReadStatus of the updated dataset that may be null.
     */
    fun putVolumeReadStatus(
        volumeID: String,
        volumeReadStatus: VolumeReadStatus.Content
    ): Response<VolumeReadStatus?> {
        return instance.putVolumeReadStatus(bearerTokenAuthentication, volumeID, volumeReadStatus)
            .execute()
    }

    /**
     * Get the publishers list from the /publishers resource.
     * Returns a Response with a PublisherResponse.List that may be null.
     */
    fun getPublishers(): Response<PublisherResponse.List?> {
        return instance.getPublishers(bearerTokenAuthentication).execute()
    }

    /**
     * Get the publisher with the specified publisherID from the /publishers/{id} resource.
     * Returns a Response with a PublisherResponse that may be null.
     */
    fun getPublisher(publisherID: String): Response<PublisherResponse?> {
        return instance.getPublisher(bearerTokenAuthentication, publisherID).execute()
    }

    /**
     * Get the volumes list of the publisher specified by publisherID from the /publishers/{id}/volumes resource.
     * Returns a Response with an VolumeResponse.List that may be null.
     */
    fun getPublisherVolumes(publisherID: String): Response<VolumeResponse.List?> {
        return instance.getPublisherVolumes(bearerTokenAuthentication, publisherID).execute()
    }

    /**
     * Retrofit-interface for the ComicLib API.
     */
    interface ComicLibApiInterface {
        @GET("tokens")
        fun getToken(@Header("Authorization") authorization: String): Call<Token?>

        @GET("issues")
        fun getIssues(@Header("Authorization") authorization: String): Call<IssueResponse.List?>

        @GET("issues/{issueID}")
        fun getIssue(@Header("Authorization") authorization: String, @Path("issueID") issueID: String): Call<IssueResponse?>

        @GET("issues/{issueID}/readstatus")
        fun getIssueReadStatus(@Header("Authorization") authorization: String, @Path("issueID") issueID: String): Call<IssueReadStatus?>

        @PUT("issues/{issueID}/readstatus")
        fun putIssueReadStatus(
            @Header("Authorization") authorization: String, @Path("issueID") issueID: String,
            @Body issueReadStatus: IssueEntity.ReadStatus
        ): Call<IssueReadStatus?>

        @PUT("issues/{issueID}/readstatus")
        fun putIssueReadStatus(
            @Header("Authorization") authorization: String, @Path("issueID") issueID: String,
            @Body issueReadStatus: IssueReadStatus.Content
        ): Call<IssueReadStatus?>

        @GET("volumes")
        fun getVolumes(@Header("Authorization") authorization: String): Call<VolumeResponse.List?>

        @GET("volumes/{volumeID}")
        fun getVolume(@Header("Authorization") authorization: String, @Path("volumeID") volumeID: String): Call<VolumeResponse?>

        @GET("volumes/{volumeID}/issues")
        fun getVolumeIssues(@Header("Authorization") authorization: String, @Path("volumeID") volumeID: String): Call<IssueResponse.List?>

        @GET("volumes/{volumeID}/readstatus")
        fun getVolumeReadStatus(@Header("Authorization") authorization: String, @Path("volumeID") volumeID: String): Call<VolumeReadStatus?>

        @PUT("volumes/{volumeID}/readstatus")
        fun putVolumeReadStatus(
            @Header("Authorization") authorization: String, @Path("volumeID") volumeID: String,
            @Body volumeReadStatus: VolumeReadStatusView
        ): Call<VolumeReadStatus?>

        @PUT("volumes/{volumeID}/readstatus")
        fun putVolumeReadStatus(
            @Header("Authorization") authorization: String, @Path("volumeID") volumeID: String,
            @Body volumeReadStatus: VolumeReadStatus.Content
        ): Call<VolumeReadStatus?>

        @GET("publishers")
        fun getPublishers(@Header("Authorization") authorization: String): Call<PublisherResponse.List?>

        @GET("publishers/{publisherID}")
        fun getPublisher(@Header("Authorization") authorization: String, @Path("publisherID") publisherID: String): Call<PublisherResponse?>

        @GET("publishers/{publisherID}/volumes")
        fun getPublisherVolumes(@Header("Authorization") authorization: String, @Path("publisherID") publisherID: String): Call<VolumeResponse.List?>
    }

}
