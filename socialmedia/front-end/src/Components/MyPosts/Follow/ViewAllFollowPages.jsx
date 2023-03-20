import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useLocation, useNavigate } from 'react-router-dom';
import ViewAllFollow from '../../../Cards/MyPosts/UserViewOn/ViewAllFollow';

import './follow.css';

const ViewAllFollowPages = () => {
    
    const {state} = useLocation();
    const navigate = useNavigate();

    const [pageList, setPageList] = useState([]);

    useEffect(() => {
        if(state){
            setPageList(state.pagesList);
        }
    }, [state])

    console.log(pageList);

    function displayPage(){
        if(pageList != ''){
            return pageList.map((eachPage) => {
                return <ViewAllFollow currPage = {eachPage} />
            })
        }
    }

    function handleClick(){
        navigate(-1);
    }

    return (
        <div>
            <div className='viewAllFPS-pages vAllFollow-mainBox justify-content-center align-items-center' >
                {displayPage()}
            </div>
            <div className='vAllFollow-Backbox'>
                <Button onClick={handleClick}>Back To Profile</Button>
            </div>
        </div>
    );
};

export default ViewAllFollowPages;