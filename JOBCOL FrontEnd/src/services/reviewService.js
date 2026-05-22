import api from "./api";

export const createReview = async (
    data
) => {

    const formData =
        new FormData();

    const reviewPayload = {

        id: data.id || null,

        rating:
            data.rating,

        comment:
            data.comment,

        imageUrl: null,

        authorType:
            data.authorType,

        reviewDate: null,

        visible:
            data.visible,

        reviewedUserId:
            data.reviewedUserId,

        reviewerId:
            data.reviewerId
    };

    formData.append(
        "review",
        new Blob(
            [
                JSON.stringify(
                    reviewPayload
                )
            ],
            {
                type:
                    "application/json"
            }
        )
    );

    if (data.image) {

        formData.append(
            "image",
            data.image
        );
    }

    const response =
        await api.post(
            "/reviews",
            formData,
            {
                headers: {
                    "Content-Type":
                        "multipart/form-data"
                }
            }
        );

    return response.data;
};

export const getReviewById = async (
    id
) => {

    const response =
        await api.get(
            `/reviews/${id}`
        );

    return response.data;
};

export const getReviewsByUser = async (
    userId
) => {

    const response =
        await api.get(
            `/reviews/contract/${userId}`
        );

    return response.data;
};

export const getReviewsByReviewer = async (
    reviewerId
) => {

    const response =
        await api.get(
            `/reviews/reviewed-user/${reviewerId}`
        );

    return response.data;
};

export const updateReview = async (
    id,
    data
) => {

    const response =
        await api.put(
            `/reviews/${id}`,
            data
        );

    return response.data;
};

export const deleteReview = async (
    id
) => {

    const response =
        await api.delete(
            `/reviews/${id}`
        );

    return response.data;
};