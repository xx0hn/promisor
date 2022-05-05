import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";
import * as S from "../styles/_index";
import ArrowBack from "../atoms/ArrowBack";
import { darkModeState } from "../states/darkmode";
import BasedTemplate from "../template/BasedTemplate";
import { AiOutlinePlus } from "react-icons/ai";
import { HiOutlineLocationMarker } from "react-icons/hi";
import { selectedGroupState } from "../states/selectedGroup";
import { RoundList } from "../organisms/RoundList";
import * as A from "../atoms/_index";
import AddModal from "../organisms/AddModal";
import { AiOutlineMenu } from "react-icons/ai";
import { ListContainer } from "../styles/Base";

const TEST_PROMISE = [
  {
    id: 1,
    title: "정기회의",
    location: "단국대학교",
    date: "2022-02-26",
  },
  {
    id: 2,
    title: "정기회의",
    location: "단국대학교",
    date: "2022-02-27",
  },
  {
    id: 3,
    title: "정기회의",
    location: "단국대학교",
    date: "2022-02-28",
  },
  {
    id: 4,
    title: "정기회의",
    location: "단국대학교",
    date: "2022-03-01",
  },
];

interface IPromise {
  month?: string;
  date?: string;
  location?: string;
}

function Promise() {
  const [showModal, setShowModal] = useState(false);

  const selectedGroup = useRecoilValue(selectedGroupState);
  const navigate = useNavigate();
  const dark = useRecoilValue(darkModeState);

  const onListClick = (index: number) => {
    navigate(`${index}`);
  };

  const onBtnClick = () => {
    // API 요청
    setShowModal(false);
  };
  const onCreateClick = () => {
    setShowModal(true);
  };
  const Header = () => {
    return (
      <>
        <PromiseTitle>
          약속<span>{selectedGroup.name}</span>
        </PromiseTitle>
      </>
    );
  };

  const Head = ({ month, date }: IPromise) => {
    return <FlatDate>{month + "/" + date}</FlatDate>;
  };

  const Container = () => {
    return (
      <>
        <ListContainer>
          {TEST_PROMISE &&
            TEST_PROMISE.map((value, index) => (
              <RoundList
                onClick={() => onListClick(index)}
                key={value.id}
                head={
                  <Head
                    month={value.date.split("-")[1]}
                    date={value.date.split("-")[2]}
                  />
                }
                main={value.title}
                sub={
                  value.location && (
                    <A.IconText
                      icon={
                        <HiOutlineLocationMarker
                          color={dark ? "#c4c4c4" : "#595959"}
                        />
                      }
                      text={value.location}
                    />
                  )
                }
              />
            ))}
        </ListContainer>
        {!showModal && (
          <A.CreateBtn value={"약속 생성"} onClick={onCreateClick} />
        )}

        <AddModal
          props={{
            titleText: "약속 생성",
            placeholderText: "어떤 약속인가요?",
            btnText: "생성",
            onBtnClick,
            showModal,
            setShowModal,
          }}
        />
      </>
    );
  };
  return <BasedTemplate header={<Header />} container={<Container />} />;
}

export default Promise;
export const PromiseTitle = styled.span`
  display: flex;
  flex-direction: column;
  align-items: center;
  span {
    color: ${(p) => p.theme.grey};
    font-size: 0.8em;
    margin-top: 0.1em;
  }
`;
const FlatDate = styled.span`
  font-size: 0.9em;
  margin-right: 1em;
  background-color: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
`;
