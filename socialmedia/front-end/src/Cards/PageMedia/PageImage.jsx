import React, { useEffect, useState } from 'react';
import { LazyLoadImage } from 'react-lazy-load-image-component';
import { useUser } from '../../UserProvider/UserProvider';

const PageImage = (props) => {
    const user = useUser();
    const {pageId, myPostId} = props;

    const [myurl, setMyurl] = useState(null);

    useEffect(() => {
        // /api/pages/${pageId}/postsTemp/${myPostId}
        fetch(`/api/pagePosts/page/${pageId}/postImg/${myPostId}`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
            .then((dataBuff) => { return dataBuff.blob(); })
            .then((data) => {
                if(data.type.split("/")[1] != 'json'){ setMyurl(URL.createObjectURL(data)); }
                return data;
            })
            .catch((error) => {console.log(error);})
    }, [pageId, myPostId]) 
    
    function displayImg(){
        if(myurl != null){
            return <LazyLoadImage  className='p-1' 
                    src={myurl}
                    style={{maxWidth:"100%", maxHeight:"32rem", width:"auto", height:"auto"}} alt="Image Alt" />
        }
    }

    return (
        <>
            {displayImg()}
        </>
    );
};

export default PageImage;